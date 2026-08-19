# GitHub Repository Governance

## Default branch

Use `main` as the protected integration branch.

Recommended branch protection/ruleset:

- require pull requests for non-emergency changes;
- require `CI` before merge;
- require `Android instrumentation` when Android UI/persistence behavior is affected;
- require `Desktop` when desktop/build/platform behavior is affected;
- require `Apple shared core` when shared-domain/build configuration changes;
- require CodeQL/security checks when available;
- require secret-scan success;
- require dependency review for dependency-graph changes;
- require conversation resolution;
- dismiss stale approvals after substantial updates where multiple maintainers exist;
- prevent force-pushes and branch deletion;
- allow administrator bypass only for documented emergencies.

Repository-level CI checks required Android/desktop/documentation/workflow paths, offline Android manifest invariants, README metadata, desktop safety/privacy structure, key privacy/backup/release invariants, internal Markdown links, repository-tooling regression tests, and exhaustive tracked-file documentation coverage.

## Documentation governance

HealthMetric treats repository documentation as a maintained interface rather than optional commentary.

Canonical ownership is defined in [`documentation-map.md`](documentation-map.md). The exhaustive tracked-file inventory is [`repository-file-reference.md`](repository-file-reference.md).

`python3 scripts/check_repository.py` runs `git ls-files` and requires each returned path to appear exactly, in backticks, in the file reference. This produces a concrete governance rule:

- adding a tracked file requires documenting its purpose;
- deleting/renaming a tracked file requires reconciling the old reference;
- configuration, workflow, test, resource and documentation files are included—there is no “source files only” exception;
- a file-reference entry should explain ownership/boundaries rather than merely mirror the filename.

The same pull request should update the topic-specific canonical document when behavior changes. For example, a new backup field belongs in `backup-format.md`; a durable persistence boundary belongs in an ADR; a workflow/release gate belongs in `testing.md`/`release.md`; an adult health-reference change belongs in `evidence.md` and shared-domain tests.

The PR template explicitly asks reviewers to verify these documentation responsibilities.

## Merge strategy

Use a normal merge commit for substantial HealthMetric work when a branch contains a carefully structured sequence of atomic Conventional Commits. This preserves reviewable history.

Squash merge remains appropriate for tiny external pull requests where intermediate commits add no review value. Do not create empty commits solely to inflate history, rewrite merged public history, or rewrite published tags.

A documentation-only commit can be meaningful when it closes a real discoverability, contract, governance, or release-readiness gap. “Maximum commits” is never a justification for empty or semantically duplicate changes.

## Suggested labels

- `bug`
- `enhancement`
- `documentation`
- `accessibility`
- `privacy`
- `security`
- `dependencies`
- `android`
- `desktop`
- `shared-core`
- `apple-core`
- `backup`
- `localization`
- `testing`
- `good first issue`
- `help wanted`
- `blocked`
- `release`

## Suggested milestones

- `v0.1.0 — Initial multi-client release`
- `v0.2.0 — Accessibility and release polish`
- `v0.3.0 — Additional client/platform work`

Milestones should describe outcomes rather than becoming a dumping ground for unrelated ideas.

## Discussions

If GitHub Discussions is enabled, suggested categories:

- Announcements
- Ideas
- Q&A
- Development

Support requests containing private health information should not be posted publicly. Point users to `SUPPORT.md` and `PRIVACY.md`.

## Issues

Use structured bug/feature forms. Security vulnerabilities must follow `SECURITY.md`, not public issues.

Issue examples, screenshots, backup reproductions, and test fixtures must use fictional data. Contributors should never attach a real HealthMetric backup containing private measurement history to a public issue.

Platform-specific reports should identify Android, Windows desktop, macOS desktop, Linux desktop, or shared-core behavior where relevant.

## Pull requests

PR descriptions should identify affected platforms and explain which verification gates apply.

A desktop-only UI change should still preserve shared-core/domain boundaries. A shared-domain change must consider Android, desktop, and configured Apple shared targets. Android persistence/backup changes require the documented privacy/backup invariants.

Reviewers should also confirm:

- new/deleted/renamed tracked files are represented in the exhaustive file reference;
- the documentation ownership matrix was consulted;
- regression tests exist for confirmed defects at the lowest practical layer;
- manual release gates are not falsely marked complete based only on automated build success.

## Releases

Tags matching `v*` trigger the automated release workflow. Only tag commits that passed the release checklist in [`release.md`](release.md), including:

- main CI;
- cross-platform Desktop workflow;
- Android connected instrumentation;
- Apple shared-core compilation;
- security workflows;
- required manual Android/desktop release-candidate checks.

The tagged workflow performs a read-only preflight, validates a stable release tag against Android/desktop versions and current `main`, builds/stages the Android and desktop artifacts, verifies the exact expected binary set, generates `SHA256SUMS.txt`, and grants repository write permission only to final publication.

Production Android signing material and any future desktop native signing/notarization credentials must remain outside repository/source history.

## Funding

Funding is optional and non-intrusive. `.github/FUNDING.yml` points to:

https://buymeacoffee.com/sanskarIN

The product must remain fully functional without donating.
