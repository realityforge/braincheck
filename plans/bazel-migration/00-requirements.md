# Bazel Migration Requirements

## Objective

Replace the Buildr-based build, test, IDE, release, and CI infrastructure with Bazel while following the structure and conventions in `/Users/peter/Code/realityforge/bazel-depgen`.

## Baseline

- Preserve commit `99c7cc0` as the migration baseline.
- Use Bazel 9.2.0, the latest stable release when the plan was accepted.
- Pin the latest `google/j2cl` `master` commit when J2CL validation is implemented.
- Use Java 25 for Bazel tooling and retain Java 17 source/bytecode compatibility for published libraries.
- Import Bazel conventions from `bazel-depgen` commit `d14bde397d5a1442b2f1e717214c5c9d2a1ab4b7`.
- Import the GWT aspect from Rose commit `77db6b5c474495c7d6bc3b6bb5cc436055901bf3`.

## Required Outcomes

1. Use DepGen-generated Java dependency repositories and targets.
2. Build and test the core and TestNG libraries with Bazel.
3. Preserve the Maven Central contract for `braincheck-core`, `braincheck-jre`, and `braincheck-testng` defined in `30-output-contract.md`.
4. Enable the reference repository's strict Error Prone checks, initially excluding NullAway.
5. Replace nullness uses of `javax.annotation` with JSpecify and enable NullAway plus explicit null-marking checks.
6. Import Java formatting, IntelliJ, release, and GitHub Actions conventions from the reference repository.
7. Add a latest-J2CL compile/link test.
8. Import Rose's GWT aspect unchanged and add compiler smoke builds for `BrainCheck` and `BrainCheckDev` through a synthetic entry point.
9. Remove legacy IDEA metadata when Bazel IntelliJ support replaces it; remove Buildr, Ruby, Travis, Node, Yarn, and GitHub Pages Javadoc deployment after full Bazel parity is established.
10. Remove the unused `org.realityforge.guiceyloops` dependency.

## Compatibility Decisions

- Preserve existing runtime and test dependency versions unless a requested toolchain, JSpecify, J2CL, or GWT integration requires a change.
- GWT validation may use current GWT dependencies instead of the legacy 2.10.0 version.
- Do not port the Javadoc cleanup task or GitHub Pages documentation deployment.
- Continue producing Maven Central Javadoc jars for core and TestNG; the JRE artifact intentionally has no Javadoc jar.
- Replace `.ipr`, `.iml`, and `.iws` files with Bazel IntelliJ/BSP metadata.
- Do not add compatibility shims for the removed build systems.

## Validation

- Every phase has a focused validation recorded in `20-task-board.yaml` and an intentional local commit.
- `tools/check.sh` is the full repository gate once introduced and must pass before completion.
- CI must run `tools/check.sh` and verify generated files are current.
- Release validation must compare archive entry manifests and normalized POMs with `30-output-contract.md`.
- J2CL validation must compile and link, not merely analyze, the core library.
- GWT validation must run the real compiler for both production and development module modes.

## Open Questions

All material questions were resolved with the user before implementation. The plan was accepted on 2026-07-17.
