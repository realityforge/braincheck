# Palantir worker migration spec

## Source and completed design tree

Authority: the requested migration, publication, assignment, and auto-merge are explicitly authorized. The user waived grill confirmation and the delivery entry gate for evidence-discoverable answers, and clarified that non-rose repositories need only Java sources reachable via the target graph.

1. Enforcement → retain existing CI → CI calls tools/check.sh → retain wrapper check command → build a root java_format_check with worker actions.
2. Dependency ownership → adopt immutable rules_palantir_java_format v0.1.1 → verified release SHA-256 e21fe1d1c5663d0ffb8e45e7bd6ebdae60016df5ce3b3a17b8187780c243324f → archive_override because BCR has no entry → formatter remains 2.93.0 → remove local formatter plumbing.
3. Coverage → JavaInfo workspace sources on supported graph edges → :all_tests plus GWT smoke plus release Javadoc binary cover 32 distinct sources → J2CL shares core sources → JRE Objects.java is a resource filegroup without JavaInfo and excluded by the adopted rule → do not create synthetic coverage or scratch-copy checks.
4. Developer commands → preserve default write and explicit check → add watch and forwarding wrapper using public external tools → preserve core/jre/testng/tools roots → check preserves worktree and Git index; write/watch preserve Git index but format working-tree files as existing write does → no specialized fixture or ahab consumer exists.
5. Delivery → isolated worktree from origin/master (be3c456) because original checkout has user edits → no open duplicate PR → plan review, committed plan, implementation with evidence, fresh implementation review, closeout deletion → assigned PR with auto-merge and existing CI.

The frontier is empty; no material user decision remains.

## Problem and required outcome

The current formatter launches a local CLI through repository-owned Maven plumbing. Replace it with the shared pinned module and reuse one Bazel formatting worker while preserving the existing CI enforcement and developer entry point.

## Scope and constraints

Only formatter migration, graph coverage, write/watch commands, dependency cleanup, required changelog, and validation are in scope. No new CI, synthetic Java targets, unrelated upgrades, source formatting churn, force merge, or settings changes. Explicit source ownership per BUILD and no glob(). rules_java/rules_jvm_external transitive minimum version increases required by the module are allowed; other upgrades are not. Preserve original checkout edits.

## Requirements

- R1: Shared pinned formatter replaces local dependencies and uses worker actions.
- R2: CI check catches unformatted JavaInfo-owned graph sources, including GWT and release tools, without modifying files.
- R3: Existing write convention and roots are preserved; public write/watch tools and watch wrapper are usable and preserve the Git index.
- R4: Publish reviewed change assigned to realityforge with auto-merge if repository settings permit, without bypassing CI.

## Acceptance criteria

- AC1 (R1): Verified archive integrity; obsolete formatter package/MODULE block/depgen references removed; lockfile regenerated without unrelated dependency upgrades; aquery and execution evidence prove PalantirJavaFormat worker actions.
- AC2 (R2): Audit 32 graph sources against actions; deliberately dirty core, GWT, and Javadoc sources fail with exact remediation; check leaves files intact, repair passes; tools/check.sh passes.
- AC3 (R3): Shell syntax and invalid mode checked; one-shot write repair, index preservation behavior, and watch startup/event behavior exercised; changelog documents command changes.
- AC4 (R4): Planning and implementation reviews pass; final diff inspected; plan and evidence commits exist; temporary plan removed in closeout commit; PR attached, assigned, auto-merge state verified.

## Significant decisions

| ID | Decision | Rationale | Impact | User verification |
| --- | --- | --- | --- | --- |
| D1 | Pin v0.1.1 immutable release | Requested shared module, verified checksum | Formatter stays 2.93.0, required transitive rule minimums rise | Review MODULE and lockfile |
| D2 | JavaInfo graph-only check | Explicit clarified scope and upstream contract | JRE resource excluded; GWT and Javadoc roots explicit | Review target list and coverage evidence |
| D3 | Public write/watch with existing roots | Preserve developer command conventions and safety | Write/watch retain JRE root; check is read-only and formatting never stages files | Review script and behavioral evidence |
| D4 | Existing CI and auto-merge | Authorized delivery with CI gating | No workflow additions or immediate merge | Review PR check and merge state |

## Testing decisions

Use focused dirty-source/repair cases with byte comparisons, execution log worker runner evidence, aquery source audit, staged and watcher exercises, buildifier, dependency regeneration, tools/check.sh, and final git diff checks. Restore all probe changes and remove scratch artifacts.

## Open questions

None.
