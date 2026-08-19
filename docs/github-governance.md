# GitHub Repository Governance

## Default branch

Use `main` as the protected integration branch.

Recommended branch protection/ruleset:

- require pull requests for non-emergency changes;
- require the `CI` verification workflow before merge;
- require `Android instrumentation` when Android UI/persistence behavior is affected;
- require `Apple shared core` when shared-domain/build configuration changes;
- require CodeQL/security checks when available;
- require secret-scan success;
- require dependency review for pull requests that change dependency graphs;
- require conversation resolution;
- dismiss stale approvals after substantial updates where multiple maintainers exist;
- prevent force-pushes and branch deletion;
- allow administrators to bypass only for documented emergencies.

The repository-level CI also checks required project/documentation paths, offline Android manifest invariants, required README metadata, key privacy documentation invariants, and internal Markdown links.

## Merge strategy

Use a normal merge commit for substantial HealthMetric work when the branch contains a carefully structured sequence of atomic Conventional Commits. This preserves reviewable development history and matches the repository's current implementation workflow.

Squash merge remains appropriate for tiny external pull requests where intermediate commits are purely iterative and add no review value. Do not create empty commits solely to inflate history, rewrite merged public history, or rewrite published release tags.

## Suggested labels

- `bug`
- `enhancement`
- `documentation`
- `accessibility`
- `privacy`
- `security`
- `dependencies`
- `android`
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

- `v0.1.0 — Initial Android release`
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

Use the repository's structured bug/feature forms. Security vulnerabilities must follow `SECURITY.md`, not public issues.

Issue examples, screenshots, backup reproductions, and test fixtures must use fictional data. Contributors should never attach a real HealthMetric backup containing private measurement history to a public issue.

## Releases

Tags matching `v*` trigger the automated release workflow. Only tag commits that passed the release checklist in [`release.md`](release.md), including Android/JVM CI, connected Android instrumentation, Apple shared-core compilation, security workflows, and the required manual release-candidate checks.

Production signing material must remain outside the repository and GitHub source history.

## Funding

Funding is optional and non-intrusive. `.github/FUNDING.yml` points to:

https://buymeacoffee.com/sanskarIN

The product must remain fully functional without donating.
