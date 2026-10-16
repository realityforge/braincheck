#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CONSUMER_DIR="$(mktemp -d)"
cleanup() {
  # Downloaded toolchains can contain read-only directories.
  find "${CONSUMER_DIR}" -type d -exec chmod u+w {} +
  rm -rf "${CONSUMER_DIR}"
}
trap cleanup EXIT

mkdir -p "${CONSUMER_DIR}/workspace/third_party/bazel/patches"
cp "${ROOT}/.bazelversion" "${CONSUMER_DIR}/workspace/"
# Match the Java toolchains without inheriting flags that name root-module repositories.
sed -n '/^common --.*java_/p; /^build --.*java/p' "${ROOT}/.bazelrc" \
  > "${CONSUMER_DIR}/workspace/.bazelrc"
cp "${ROOT}/third_party/bazel/patches/BUILD.bazel" \
  "${ROOT}/third_party/bazel/patches/j2cl_fix_escapevelocity_coordinate.patch" \
  "${CONSUMER_DIR}/workspace/third_party/bazel/patches/"

cat > "${CONSUMER_DIR}/workspace/MODULE.bazel" <<EOF_MODULE
module(name = "braincheck_consumer")
bazel_dep(name = "braincheck")
local_path_override(module_name = "braincheck", path = "${ROOT}")
EOF_MODULE

# Overrides only apply in the root module. Reuse the production dependency pins,
# including the J2CL archive and its patch, without supplying development tools.
{
  echo 'archive_override('
  sed -n '/^    module_name = "j2cl",/,/^)/p' "${ROOT}/MODULE.bazel"
  sed -n '/^single_version_override(/,/^)/p' "${ROOT}/MODULE.bazel"
} >> "${CONSUMER_DIR}/workspace/MODULE.bazel"

cat > "${CONSUMER_DIR}/workspace/BUILD.bazel" <<'EOF_BUILD'
filegroup(
    name = "publication",
    srcs = [
        "@braincheck//:LICENSE",
        "@braincheck//:artifacts",
        "@braincheck//tools/release:maven_artifacts",
    ],
)
EOF_BUILD

cd "${CONSUMER_DIR}/workspace"
BAZEL=(bazel --batch --output_base="${CONSUMER_DIR}/output_base")
"${BAZEL[@]}" mod graph > "${CONSUMER_DIR}/module-graph.txt"
if rg -q '(buildifier_prebuilt|rules_palantir_java_format)@' "${CONSUMER_DIR}/module-graph.txt"; then
  echo "External consumer unexpectedly includes a development formatter" >&2
  exit 1
fi
"${BAZEL[@]}" build --symlink_prefix="${CONSUMER_DIR}/outputs/" //:publication
