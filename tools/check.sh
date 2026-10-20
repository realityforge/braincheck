#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "${ROOT}"

tools/update_java_deps.sh
bazel run //tools/buildifier:buildifier_check
tools/java_format.sh check
bazel build //...
bazel build //core/src/test/j2cl:braincheck_j2cl_smoke
bazel build //core/src/test/gwt:braincheck_gwt_assets //core/src/test/gwt:braincheck_dev_gwt_assets
bazel test //...
tools/check_bazel_consumer.sh
