# Bazel Migration Implementation Plan

## Phase Sequence

1. Copy repository instructions.
2. Add the Bazel 9.2 build, DepGen dependencies, Java/TestNG tests, artifact targets, and standard check script.
3. Fix findings from the strict Error Prone policy, if any, then add and enable that policy without NullAway.
4. Migrate nullness annotations to JSpecify, remove `javax.annotation`, and enable NullAway/JSpecify checks.
5. Apply formatter-only changes, then add Java-format tooling.
6. Replace legacy IDEA metadata with Bazel IntelliJ/BSP tooling.
7. Add Maven Central release rules, builders, tests, and lifecycle scripts for all three artifacts.
8. Add a latest-J2CL compile/link test pinned to an exact upstream commit.
9. Import Rose's GWT aspect and add production/development compiler smoke targets.
10. Add the reference GitHub Actions workflow.
11. Remove Buildr/Ruby/Travis infrastructure, then remove Node/Yarn/Javadoc-site infrastructure.
12. Run implementation alignment review and full validation, resolve findings, then delete this completed plan tree in a cleanup-only commit.

## Delivery Approach

- Keep one task in progress and one focused commit per accepted phase boundary.
- Preserve the legacy build until Bazel build, test, release, J2CL, GWT, IDE, and CI paths exist.
- Generate dependency declarations with DepGen; do not hand-maintain generated sections.
- Follow package-owned Bazel targets, explicit source lists, and no `glob()` usage.
- Copy reference tooling where requested, adapting only project coordinates, targets, and artifact topology.
- Inspect staged diffs and run normal hooks before each commit.

## High-Risk Areas

- Published artifact parity:
  - Impact: missing resources, sources, POM dependencies, or classifiers would break consumers or Maven Central publication.
  - Mitigation: compare generated archive entry manifests and normalized POMs with `30-output-contract.md` and add release integration tests.
- J2CL on Bazel 9.2:
  - Impact: upstream J2CL follows `master` and may require compatibility patches.
  - Mitigation: pin an exact current commit and import only evidenced patches needed for compile/link validation.
- GWT source/resource collection:
  - Impact: a target that analyzes but does not run the compiler would not prove the aspect works.
  - Mitigation: build real compiler outputs for synthetic entry points in both module modes.
- Nullness migration:
  - Impact: JSpecify changes public annotations and NullAway can expose contract errors.
  - Mitigation: migrate package defaults and type-use annotations together, then run all Java tests and artifact builds.
- Release replacement:
  - Impact: removing Buildr before Bazel publication parity would strand releases.
  - Mitigation: keep Buildr until release lifecycle and dry-run packaging checks pass.

## Required Full Gate

`tools/check.sh`

## Completion Criteria

- All accepted phases are committed and their evidence is recorded.
- `tools/check.sh` passes with Bazel 9.2.0.
- Generated dependency and lock files are current.
- The three Maven artifact families, J2CL link test, and both GWT smoke builds pass.
- Buildr, Ruby, Travis, legacy IDEA, Node, Yarn, and Javadoc-site files are absent.
- Implementation alignment review has no actionable findings.
- The plan tree is removed in a deletion-only cleanup commit and the worktree is clean.
