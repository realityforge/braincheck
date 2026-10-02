# Task Map

- Spec: [SPEC.md](../SPEC.md)
- Status: `implementation-review`
- Current frontier: `None`
- Planning reviewer: `/root/planning_reviewer` (`1/3` rounds, `Findings: none`)
- Plan checkpoint: `automatic` (completed evidence-based design tree, explicit grill/entry exception, passing planning review)
- Implementation reviewer: `pending` (`0/5` rounds)

## Full-scope validation

- Gate: `tools/check.sh`
- Evidence: final isolated `tools/check.sh` exited 0; all builds including J2CL and both GWT configurations passed; all 7 test targets passed. Earlier disk/network/stale shared-extraction failures recovered with task-local cache isolation. See T01 evidence.

## Tasks

| ID | Task | Status | Blocked by |
| --- | --- | --- | --- |
| T01 | [Migrate and validate formatter](T01-migrate.md) | complete | None |

## Sequencing notes

One coherent migration preserves CI through the wrapper while replacing dependency ownership. Publication follows passing implementation review and closeout.

## Promoted knowledge

not-required: no domain documentation directories or configuration exist.
