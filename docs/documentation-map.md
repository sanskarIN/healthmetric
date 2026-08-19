# Documentation Map and Maintenance Guide

This document explains how HealthMetric documentation is organized, which document is authoritative for each concern, and what must be updated when behavior changes. It is intentionally maintenance-oriented: contributors should be able to identify the correct documentation surface before changing code.

## Documentation principles

HealthMetric documentation follows five rules:

1. **Code and tests define implemented behavior.** Documentation must describe that behavior without promising capabilities that are not present.
2. **One canonical document owns each detailed contract.** Other documents may summarize and link to it, but should not fork the rules.
3. **Privacy, adult-use safety, evidence, and release claims must be conservative.** Automation success is not a substitute for physical-device, accessibility, signing, notarization, or human review where those are required.
4. **Every tracked repository file must be documented.** [`repository-file-reference.md`](repository-file-reference.md) is the exhaustive file-by-file inventory and is checked by repository automation.
5. **Behavior changes and documentation changes should land together.** A confirmed defect should receive regression coverage and any affected public/developer contract should be updated in the same change set.

## Start here by audience

### Users and evaluators

- [`../README.md`](../README.md) — product scope, capabilities, privacy posture, platform status, build entry points, project links, and top-level limitations.
- [`../PRIVACY.md`](../PRIVACY.md) — data collection/storage/export boundaries and platform-specific privacy behavior.
- [`../SECURITY.md`](../SECURITY.md) — vulnerability reporting and security assumptions.
- [`../SUPPORT.md`](../SUPPORT.md) — support channels and issue-routing expectations.
- [`accessibility.md`](accessibility.md) — implemented accessibility behavior and release-candidate manual checks.
- [`evidence.md`](evidence.md) — health-reference provenance, review policy, and evidence-update expectations.

### Contributors

- [`../CONTRIBUTING.md`](../CONTRIBUTING.md) — contribution process and required quality expectations.
- [`setup.md`](setup.md) — workstation/toolchain setup.
- [`development.md`](development.md) — day-to-day engineering workflow, module responsibilities, and verification commands.
- [`architecture.md`](architecture.md) — system boundaries, dependency direction, data flow, persistence decisions, and platform separation.
- [`testing.md`](testing.md) — automated/manual test matrix and regression policy.
- [`design-system.md`](design-system.md) — Android UI tokens/components and visual consistency rules.
- [`desktop.md`](desktop.md) — desktop-specific architecture, privacy model, behavior, packaging, and manual verification.
- [`repository-file-reference.md`](repository-file-reference.md) — purpose and maintenance notes for every tracked file.

### Maintainers and release owners

- [`github-governance.md`](github-governance.md) — repository settings, branch/review expectations, automation ownership, and governance checks.
- [`release.md`](release.md) — release preflight, build artifacts, tag validation, signing boundaries, manual acceptance, and rollback.
- [`backup-format.md`](backup-format.md) — authoritative Android portable-backup schema and restore invariants.
- [`performance.md`](performance.md) — performance assumptions, measurement points, and optimization guardrails.
- [`troubleshooting.md`](troubleshooting.md) — common development/build/runtime failure paths.
- [`../CHANGELOG.md`](../CHANGELOG.md) — user/developer-visible changes not yet released and release history.
- [`../ROADMAP.md`](../ROADMAP.md) — completed and remaining milestones without presenting unfinished release gates as complete.
- [`../what_changed.md`](../what_changed.md) — continuation/handoff record for the active implementation history.

## Canonical ownership by topic

| Topic | Canonical document | Supporting documents |
|---|---|---|
| Product scope and entry points | `README.md` | `architecture.md`, `desktop.md` |
| Android privacy/local data | `PRIVACY.md` | `architecture.md`, `backup-format.md`, ADR 0002/0004 |
| Portable backup schema | `docs/backup-format.md` | `testing.md`, `release.md`, ADR 0004 |
| Shared calculation architecture | `docs/architecture.md` | ADR 0001/0003, `evidence.md` |
| Adult reference evidence | `docs/evidence.md` | ADR 0003, shared domain tests |
| Android visual system | `docs/design-system.md` | `accessibility.md` |
| Desktop scope/privacy | `docs/desktop.md` | ADR 0005, `architecture.md` |
| Development workflow | `docs/development.md` | `setup.md`, `testing.md`, `troubleshooting.md` |
| Test coverage/regressions | `docs/testing.md` | workflow YAML, source-level tests |
| Accessibility acceptance | `docs/accessibility.md` | `release.md`, UI tests |
| Performance policy | `docs/performance.md` | `testing.md`, `architecture.md` |
| Release process/artifacts | `docs/release.md` | `.github/RELEASE_TEMPLATE.md`, workflow YAML |
| Repository governance | `docs/github-governance.md` | `CONTRIBUTING.md`, issue/PR templates |
| File-level ownership | `docs/repository-file-reference.md` | this document |
| Architectural decisions | `docs/adr/*.md` | `architecture.md` |
| Continuation history | `what_changed.md` | `CHANGELOG.md`, `ROADMAP.md` |

## Documentation update matrix

Use this matrix before merging a behavior change. More than one row may apply.

| Change type | Minimum documentation to review/update |
|---|---|
| Add, delete, or rename any tracked file | `repository-file-reference.md`; repository invariant expectations |
| Add/change Android screen or navigation | `README.md` if user-visible, `architecture.md`, `testing.md`, `accessibility.md`, file reference |
| Add/change Android persistence | `PRIVACY.md`, `architecture.md`, `backup-format.md` when portable data is affected, ADR if architectural, tests |
| Change backup JSON/schema/validation | `backup-format.md`, `PRIVACY.md`, `testing.md`, `release.md`, `CHANGELOG.md`, ADR 0004 or successor |
| Change adult-use gate | `README.md`, `PRIVACY.md` where data state changes, `testing.md`, `accessibility.md`, `CHANGELOG.md` |
| Change health calculation/reference rules | `evidence.md`, `architecture.md`, `testing.md`, `CHANGELOG.md`, ADR 0003 or successor |
| Change shared targets/dependency direction | `architecture.md`, `setup.md`, `development.md`, CI docs, ADR 0001 or successor |
| Change desktop behavior | `desktop.md`, `architecture.md`, `testing.md`, `release.md` if packaging/release affected |
| Change UI tokens/components | `design-system.md`, `accessibility.md`, screenshots/testing when visual evidence changes |
| Change dependencies/toolchain | `setup.md`, `development.md`, `troubleshooting.md` when relevant, version catalog comments/usage |
| Change CI workflow | `testing.md`, `github-governance.md`, `release.md` for release gates, file reference |
| Change release artifacts/tagging | `release.md`, `.github/RELEASE_TEMPLATE.md`, `testing.md`, `CHANGELOG.md` |
| Change security/privacy controls | `SECURITY.md`, `PRIVACY.md`, `architecture.md`, tests/invariants, `CHANGELOG.md` |
| Add a known limitation | `README.md` or platform doc, `ROADMAP.md` when planned, `release.md` if release-impacting |

## ADR policy

Architecture Decision Records live under `docs/adr/` and are append-oriented. Existing ADRs represent decisions already made and should not be silently rewritten to describe a materially different architecture.

Create a new ADR when a change alters a durable boundary such as:

- where calculations live;
- whether/where user data persists;
- whether consent/safety state is portable;
- the evidence/reference versioning model;
- desktop persistence policy;
- a new network/cloud/account boundary;
- a new security-sensitive release/signing design.

A newer ADR may supersede an older one. The architecture document should link to the current decision and preserve the historical record.

## Documentation accuracy rules

Do not claim:

- a workflow is green unless the exact commit was observed green;
- a screenshot is publication-approved merely because CI generated it;
- an APK/AAB or desktop package is production-signed unless protected signing actually happened;
- an iOS application exists merely because the shared module compiles for iOS targets;
- desktop persistence or Android/desktop backup interoperability unless it is implemented and tested;
- medical diagnosis, treatment, personal body targets, or appearance scoring.

Use exact platform wording. Android currently has opt-in local history and portable JSON backup. Desktop intentionally keeps HealthMetric measurement/session state ephemeral and has no HealthMetric backup layer.

## Keeping the file reference complete

`docs/repository-file-reference.md` contains one exact backticked repository path for every tracked file. `scripts/check_repository.py` compares that documentation against `git ls-files` during verification.

When adding a tracked file:

1. add the file for a defined purpose;
2. add its exact path and purpose to the file reference;
3. update any topic-specific canonical documentation from the matrix above;
4. add/adjust tests or repository invariants where the new file establishes a durable requirement;
5. run the repository and Markdown-link audits.

When deleting or renaming a tracked file, remove/update the old reference at the same time. A stale reference is misleading even if the replacement behavior remains valid.

## Documentation verification

Portable checks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

Full non-device verification:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

See [`testing.md`](testing.md) and [`release.md`](release.md) for platform-specific and manual verification that cannot be reduced to these commands.
