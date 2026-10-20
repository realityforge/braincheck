#!/usr/bin/env bash
set -euo pipefail

root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
scratch="$(mktemp -d "${TMPDIR:-/tmp}/braincheck-bazel-consumer.XXXXXX")"
workspace="$scratch/application"
mkdir "$workspace"
cleanup() {
  (cd "$workspace" && bazel --output_base="$scratch/bazel" shutdown >/dev/null 2>&1) || true
  find "$scratch" -type d -exec chmod u+w {} +
  rm -rf "$scratch"
}
trap cleanup EXIT

# Inspect visibility from another repository, including source files and macro companions.
(cd "$root" && bazel query 'visible(@rules_java//java:defs.bzl, //...:*)' --output=xml) > "$scratch/public.xml"
sed -nE \
  -e 's/^[[:space:]]*<generated-file generating-rule="([^"]+)".*/\1/p' \
  -e 's/^[[:space:]]*<(rule|source-file) .* name="([^"]+)".*/\2/p' \
  "$scratch/public.xml" | LC_ALL=C sort -u > "$scratch/public.actual"
printf '%s\n' '//:core' '//:core-j2cl' '//:testng' > "$scratch/public.expected"
diff -u "$scratch/public.expected" "$scratch/public.actual"

cp "$root/.bazelversion" "$workspace/"
sed '/--flag_alias=release_version=/d' "$root/.bazelrc" > "$workspace/.bazelrc"
cat > "$workspace/MODULE.bazel" <<MODULE
module(name = "braincheck_consumer")
bazel_dep(name = "braincheck")
local_path_override(module_name = "braincheck", path = "$root")
MODULE
# Reuse the root toolchain versions and required overrides, without its development dependencies.
awk '/^bazel_dep\(/ && !/dev_dependency/ { print }
     /^archive_override\(/ { block = $0 ORS; collecting = 1; next }
     /^single_version_override\(/ { block = $0 ORS; collecting = 1; next }
     collecting { block = block $0 ORS; if ($0 == ")") {
       if (block !~ /rules_palantir_java_format/) printf "%s", block
       collecting = 0
     }}' "$root/MODULE.bazel" >> "$workspace/MODULE.bazel"
# A root Maven install merges J2CL and Closure's tool dependencies; otherwise
# rules_jvm_external can select a dependency module's partial repository.
sed -n '/^j2cl_maven = /,/^use_repo(j2cl_maven/p' "$root/MODULE.bazel" >> "$workspace/MODULE.bazel"
mkdir -p "$workspace/third_party/bazel/patches"
cp "$root/third_party/bazel/patches/j2cl_fix_escapevelocity_coordinate.patch" "$workspace/third_party/bazel/patches/"
printf '%s\n' 'exports_files(["j2cl_fix_escapevelocity_coordinate.patch"], visibility = ["//:__pkg__"])' > "$workspace/third_party/bazel/patches/BUILD.bazel"

cat > "$workspace/BUILD.bazel" <<'BUILD'
load("@rules_java//java:defs.bzl", "java_binary")
load("@j2cl//build_defs:rules.bzl", "j2cl_application")
load("@rules_closure//closure:defs.bzl", "closure_js_library")

java_binary(
    name = "consumer",
    srcs = ["Consumer.java"],
    main_class = "Consumer",
    jvm_flags = ["-Dbraincheck.environment=development"],
    deps = ["@braincheck//:core", "@braincheck//:testng"],
)

alias(name = "direct", actual = "@braincheck//:core-j2cl")
alias(name = "transitive", actual = "@braincheck//:core-j2cl")
closure_js_library(
    name = "entry",
    srcs = ["entry.js"],
    deps = [":direct", ":transitive"],
)
j2cl_application(
    name = "browser",
    entry_points = ["braincheck.j2cl.BuildTest"],
    deps = [":entry"],
)
BUILD
cat > "$workspace/Consumer.java" <<'JAVA'
import java.io.File;
import org.realityforge.braincheck.Guards;
import org.realityforge.braincheck.GuardMessageCollector;

public final class Consumer {
    public static void main(String[] args) {
        GuardMessageCollector collector = new GuardMessageCollector("Consumer", new File("unused.json"));
        collector.onTestStart();
        try {
            Guards.invariant(() -> false, () -> "consumer invariant");
            throw new AssertionError("Invariant checks did not run");
        } catch (IllegalStateException expected) {
            if (!"consumer invariant".equals(expected.getMessage())) {
                throw new AssertionError(expected);
            }
        }
        collector.onTestComplete();
    }
}
JAVA
cp "$root/core/src/test/j2cl/entry_point.js" "$workspace/entry.js"
cd "$workspace"
consumer_bazel() {
  bazel --output_base="$scratch/bazel" "$@"
}
consumer_bazel run //:consumer
consumer_bazel build //:browser

expect_failure() {
  local expected="$1"
  if consumer_bazel build --nobuild //denied:target > "$scratch/denied.log" 2>&1; then
    echo 'Expected external access to be rejected' >&2
    exit 1
  fi
  if ! grep -Fq "$expected" "$scratch/denied.log"; then
    cat "$scratch/denied.log" >&2
    exit 1
  fi
}
mkdir denied
for label in \
  '//core/src/main/java/org/realityforge/braincheck:core' \
  '//core/src/main/java/org/realityforge/braincheck:core-j2cl' \
  '//core/src/main/java/org/realityforge/braincheck:Guards.java' \
  '//testng/src/main/java/org/realityforge/braincheck:testng' \
  '//tools/release:braincheck_core_jar' \
  '//third_party/java:dependencies.yml'; do
  printf 'filegroup(name = "target", srcs = ["@braincheck%s"])\n' "$label" > denied/BUILD.bazel
  expect_failure 'not visible'
done
for entry in \
  'third_party/java:rules.bzl java_library' \
  'tools/gwt:defs.bzl gwt_compile' \
  'tools/release:release.bzl release_jar'; do
  read -r label symbol <<< "$entry"
  printf 'load("@braincheck//%s", "%s")\nfilegroup(name = "target")\n' "$label" "$symbol" > denied/BUILD.bazel
  expect_failure 'not visible for loading'
done
printf '%s\n' 'External JVM/J2CL consumer and public Bazel boundaries passed'
