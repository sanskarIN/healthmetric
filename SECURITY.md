# Security Policy

## Supported versions

Security fixes are applied to the latest development line and the latest published release when practical.

| Version | Supported |
|---|---|
| `main` | Yes |
| Latest release | Yes |
| Older releases | Best effort |

## Reporting a vulnerability

Do **not** open a public issue for a vulnerability that could expose user data, enable code execution, compromise release artifacts, or reveal security-sensitive details.

Report privately to:

- `sanskarin@outlook.in`
- `sanskarin.business@gmail.com`

Include:

1. affected version/commit;
2. affected component/platform;
3. impact description;
4. minimal reproduction using fictional data;
5. suggested mitigation if known.

Do not send real health records, credentials, tokens, signing material, or other private user data.

## Security design

HealthMetric intentionally minimizes attack surface:

- core calculators require no backend;
- adult reference calculators are gated behind explicit adult-use choices in user-facing clients;
- Android requests no Internet permission and disables cleartext traffic;
- no account/authentication system is required;
- no advertising or analytics tracker SDK is included;
- Android application backup is disabled;
- Android local history is disabled by default, opt-in, bounded, selectively deletable, and fully erasable;
- Android export/restore is explicit and user-initiated;
- Android restore requires confirmation before portable data is replaced;
- Android backup reads/writes are limited to 1 MiB;
- restored Android JSON is schema/structure checked, record-validated, deduplicated, chronologically normalized, and history-bounded;
- malformed backup structure or a non-empty all-invalid history array fails before DataStore mutation;
- portable Android restore cannot change current history opt-in, adult-use confirmation, or onboarding safety state;
- raw weight, height, and waist inputs are not persisted in Android history;
- extreme finite imported history values are normalized safely before chart drawing rather than trusted as raw Canvas coordinates;
- stale saved Android enum names fall back safely instead of crashing composition;
- desktop measurements/results/adult-use/theme/navigation state are not persisted;
- desktop input parsing rejects signed/scientific/non-finite measurement syntax and invalid split remaining-inch components;
- desktop external links require an explicit button action;
- backup contents and measurement values are not intentionally written to logs;
- signing keys and secrets are excluded from source control;
- release tags/assets are validated before publication and release write permission is scoped to the final publish job;
- CodeQL, dependency review, full-history secret scanning, Android instrumentation, desktop multi-OS verification, Apple shared-core compilation, and standard build/test checks run in GitHub Actions;
- every tracked repository file must be documented by the exhaustive file-reference invariant;
- Dependabot monitors Gradle and GitHub Actions dependencies.

See [`docs/backup-format.md`](docs/backup-format.md), [`docs/desktop.md`](docs/desktop.md), [`docs/architecture.md`](docs/architecture.md), ADR 0004, and ADR 0005 for platform trust boundaries.

## Android imported-data threat model

A selected Android backup document is untrusted input even though the user explicitly chose it.

HealthMetric therefore:

- bounds the UTF-8 payload before JSON parsing;
- accepts only the supported top-level schema version;
- requires top-level `history` to be a JSON array before persistence mutation;
- distinguishes an explicit empty array from a damaged non-empty array;
- rejects a non-empty history array when no valid entry survives sanitation;
- normalizes retention/theme preferences to supported values;
- validates every history record independently when the container is structurally valid;
- rejects invalid/blank IDs, negative timestamps, non-finite numeric values, and unknown calculator kinds;
- caps record field lengths;
- deduplicates record IDs before they reach Compose list keys;
- sorts valid history newest-first before applying retention;
- caps final restored history;
- keeps consent/safety state device-local;
- performs structural/content validation before opening the DataStore mutation;
- performs the DataStore mutation only after explicit user confirmation.

A future schema must retain equivalent protections or record the reviewed replacement in an ADR.

## Android presentation-state threat boundary

Two defensive UI boundaries are intentionally tested:

- `ChartScale` prevents extreme finite imported history values from overflowing chart normalization arithmetic;
- `savedEnumValueOrDefault` prevents stale persisted Compose navigation/filter names from crashing after future enum renames/removals.

These defenses do not change the imported backup values or silently relax schema validation; they keep presentation arithmetic/state restoration robust after valid data reaches the UI.

## Desktop trust boundary

The desktop client intentionally avoids a local measurement-data persistence/import surface. Its primary runtime inputs are user-entered text and explicit external-link actions.

Controls include:

- parsing malformed, signed, scientific-notation, and non-finite measurement text before shared calculation;
- requiring whole feet plus remaining inches in `[0, 12)` for split imperial height entry;
- retaining authoritative adult range validation in `shared`;
- separating the under-18 path from adult reference calculators;
- keeping calculator/adult/theme/navigation state in process memory only;
- opening external URLs only on explicit user action;
- testing desktop adapter/parsing logic;
- building/testing desktop code on Linux, Windows, and macOS CI.

If desktop persistence/import is introduced later, its untrusted-input and storage threat model must be documented before release and ADR 0005 must be superseded or explicitly updated.

## External intents and links

Android GitHub/release/funding/email links and backup share destinations are opened only after explicit user actions.

Desktop evidence/repository/funding links are likewise opened only after explicit button actions.

HealthMetric does not guarantee the privacy/security behavior of selected external applications or destinations.

## Secret handling

Never commit:

- API keys or access tokens;
- signing keystores/passwords;
- desktop signing/notarization credentials;
- private production endpoints;
- personal health information;
- generated credentials;
- private certificates/keys.

`.env.example` contains placeholders/documentation only. Store release credentials outside the repository and inject them through protected local/CI secret mechanisms.

Repository-history secret scanning is intended to catch accidental committed material; deleting a secret from the latest tree is not sufficient once it has entered public Git history. Rotate/revoke exposed credentials as appropriate.

## Dependency policy

Prefer maintained dependencies from trusted sources. Dependency upgrades should pass the full applicable test/lint/build suite before merge. High-severity dependency-review findings block pull requests unless a documented false positive or accepted temporary exception exists.

Dependency changes affecting Compose Multiplatform must pass the three-OS Desktop workflow; shared Kotlin changes must also remain compatible with the configured Apple targets.

## Repository/documentation integrity

[`docs/repository-file-reference.md`](docs/repository-file-reference.md) documents every tracked file's purpose and security/privacy/release participation where relevant. `scripts/check_repository.py` compares that reference with `git ls-files`.

This is not a substitute for code review, but it prevents newly tracked scripts, workflows, configuration, persistence code, or assets from remaining structurally undocumented while repository verification is green.

[`docs/documentation-map.md`](docs/documentation-map.md) defines which canonical security/privacy/architecture/release documents must be reviewed when durable behavior changes.

## Release security gates

Before tagging, the exact release commit should have successful:

- CI repository/docs audits (including exhaustive tracked-file coverage), repository-tooling tests, formatting, shared/desktop/Android tests, desktop JAR packaging, Android lint, APK and AAB assembly;
- Desktop Linux/Windows/macOS JAR + native-package matrix;
- Android emulator instrumentation and required screenshot artifact generation;
- Apple shared-core compilation;
- CodeQL analysis;
- dependency review where applicable;
- repository-history secret scanning.

Tagged release preflight additionally requires:

- stable `vMAJOR.MINOR.PATCH` syntax;
- tag version agreement with Android and desktop project versions;
- tag commit equal to current `main`;
- complete history checkout for the tag/main comparison;
- read-only repository permission until final publication;
- deterministic staging with exactly one non-empty expected build output per artifact type;
- an exact final eight-binary asset set with no extras/missing/empty files;
- `SHA256SUMS.txt` generation and Git tag verification before release creation.

Manual release security checks must also confirm:

- Android production signing remains outside source control;
- any future desktop native signing/notarization credentials remain outside source control;
- published desktop artifacts were manually launched on their target platform;
- Android release-candidate behavior was checked on physical hardware;
- accessibility/screenshot review does not expose private data or misleading health claims;
- no unexpected persistence/network behavior was introduced.

A green superseded commit is not security/release evidence for a newer branch head.

## Disclosure process

Maintainers will validate the report, determine severity, prepare a fix, add regression coverage where feasible, update affected documentation/contracts, and publish release notes without unnecessary exploit detail. Reporters may be credited with permission.
