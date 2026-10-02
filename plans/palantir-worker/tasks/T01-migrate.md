# T01 — Migrate and validate formatter

- Status: complete
- Blocked by: None
- Spec coverage: R1–R4, AC1–AC4

## Delivers

Shared worker check in existing CI, public write/watch wrappers, removed local dependency plumbing, regenerated locks, changelog, reviewed assigned auto-merge PR.

## Acceptance criteria

- [x] AC1 shared module and worker execution with obsolete plumbing removed.
- [x] AC2 graph coverage, negative checks, full gate.
- [x] AC3 safe write/watch behavioral evidence and changelog.
- AC4: local implementation and evidence ready for review; implementation review, closeout, and PR metadata verified by the following workflow gates.

## Validation

Archive checksum; shell syntax; buildifier; aquery/execution logs; dirty core/GWT/Javadoc and repair; staged safety; watcher event; dependency regeneration and tools/check.sh; final diff and PR metadata.

## Evidence

- Release archive independently downloaded and SHA-256 verified: e21fe1d1c5663d0ffb8e45e7bd6ebdae60016df5ce3b3a17b8187780c243324f. BCR metadata request returned 404.
- `bash -n tools/java_format.sh tools/java_format_watch.sh tools/update_java_deps.sh` passed; invalid wrapper mode returned 2 with usage.
- Buildifier passed. `bazel mod deps --lockfile_mode=update` regenerated the lockfile; direct rules_java/rules_jvm_external versions align with module-required 9.9.0/7.1. JSON parsing passed.
- `bazel build //:java_format_check --execution_log_json_file=... --worker_verbose` passed. Execution log contains 11 PalantirJavaFormat actions, all with runner worker; verbose log records one singleplex worker.
- `bazel aquery 'mnemonic("PalantirJavaFormat", deps(//:java_format_check))' --output=jsonproto` lists exactly 32 distinct tracked Java sources. Only JRE resource-only Objects.java is excluded; GWT and Javadoc sources included; J2CL shares checked core sources.
- Appending deliberately unformatted valid Java to core BrainCheckUtil.java, GWT SmokeEntryPoint.java, and release JavadocJarBuilder.java made check exit 1 and report all three paths plus `tools/java_format.sh write`. Byte comparisons prove check leaves all three unchanged; Git blob comparisons prove check and write leave a staged probe unchanged in the index. Write repairs all three; subsequent check passes. Probe files and index restored.
- Initial write compilation exposed default Java 11 language level rejecting upstream records; .bazelrc now selects Java 17 language/runtime (existing application baseline) and retains tooling Java 25. Repeated write/check probes passed.
- Watch wrapper readiness reports the four original roots; a preexisting dirty probe remains unchanged at startup, then formats on a modification event. Watch process stopped and probe removed. Final formatter check passed.
- No generated fixture consumer, ahab config, or domain-doc directories exist; no such migration or promotion required.
- Simplify preflight found no justified additional abstraction or edit.
- Full gate `tools/check.sh` passed on the final isolated run (exit 0): dependency regeneration, Buildifier, formatter, `bazel build //...`, J2CL smoke, production/development GWT assets, `bazel test //...` (7/7 tests passed).
- Earlier attempts failed on full disk and a Maven connection reset; after pause/resume a stale shared JDK extraction path failed. Removed only task-owned duplicate dependency caches, reused the existing Maven cache, and isolated extracted repository contents. Forced refetch and final full gate passed. No product/config workaround for those environmental failures was added.
- Final `git diff --check` passed. Generated third_party/java BUILD and all Java source files match the base; no source formatting churn. Remote master remains be3c456.

Local Bazel checks use a temporary PATH wrapper adding `--output_base=/tmp/braincheck-palantir-output` and a temporary rc with `--repo_contents_cache=/tmp/braincheck-palantir-contents`, avoiding shared-output and stale extraction contention. The final full gate uses `BAZEL_OUTPUT_BASE=/Users/peter/.bazel` for the updater’s existing dependency cache only. Evidence logs live outside the worktree under /tmp/braincheck-palantir-reference and are supporting data, not requirements.

- Implementation review `/root/implementation_reviewer`, round 1/5: Findings: none. Checked complete diff, upstream contracts, checksum/lockfile, coverage, worker logs, probes, full gate; independently reran the format check successfully. Publication remains the authorized post-closeout delivery gate.
