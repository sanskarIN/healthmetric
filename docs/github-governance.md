# GitHub Repository Governance

## Default branch

Use `main` as the protected integration branch.

Recommended branch protection/ruleset:

- require pull requests for non-emergency changes;
- require CI verification before merge;
- require CodeQL/security checks when available;
- dismiss stale approvals after substantial updates where multiple maintainers exist;
- prevent force-pushes and branch deletion;
- require conversation resolution;
- allow administrators to bypass only for documented emergencies.

## Merge strategy

Prefer squash merge for small feature/fix pull requests when preserving every branch commit is not valuable. Use normal merge when a carefully structured multi-commit history is meaningful. Do not rewrite published release tags.

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
- `testing`
- `good first issue`
- `help wanted`
- `blocked`
- `release`

## Suggested milestones

- `v0.1.0 — Initial Android release`
- `v0.2.0 — Persistence and accessibility depth`
- `v0.3.0 — Multiplatform expansion`

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

## Releases

Tags matching `v*` trigger the automated release workflow. Only tag commits that passed the release checklist in `docs/release.md`.

## Funding

Funding is optional and non-intrusive. `.github/FUNDING.yml` points to:

https://buymeacoffee.com/sanskarIN

The product must remain fully functional without donating.
