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
- restored Android JSON is schema-checked, record-validated, deduplicated, chronologically normalized, and history-bounded;
- portable Android restore cannot change current history opt-in, adult-use confirmation, or onboarding safety state;
- raw weight, height, and waist inputs are not persisted in Android history;
- desktop measurements/results/adult-use/theme/navigation state are not persisted;
- desktop external links require an explicit button action;
- backup contents and measurement values are not intentionally written to logs;
- signing keys and secrets are excluded from source control;
- CodeQL, dependency review, full-history secret scanning, Android instrumentation, desktop multi-OS verification, Apple shared-core compilation, and standard build/test checks run in GitHub Actions;
- Dependabot monitors Gradle and GitHub Actions dependencies.

See [`docs/backup-format.md`](docs/backup-format.md), [`docs/desktop.md`](docs/desktop.md), ADR 0004, and ADR 0005 for platform trust boundaries.

## Android imported-data threat model

A selected Android backup document is untrusted input even though the user explicitly chose it.

HealthMetric therefore:

- bounds the UTF-8 payload before JSON parsing;
- accepts only the supported top-level schema version;
- normalizes retention/theme preferences to supported values;
- validates every history record independently;
- rejects invalid/blank IDs, negative timestamps, non-finite numeric values, and unknown calculator kinds;
- caps record field lengths;
- deduplicates record IDs before they reach Compose list keys;
- sorts valid history newest-first before applying retention;
- caps final restored history;
- keeps consent/safety state device-local;
- performs the DataStore mutation only after user confirmation.

A future schema must retain equivalent protections or record the reviewed replacement in an ADR.

## Desktop trust boundary

The desktop client intentionally avoids a local measurement-data persistence/import surface. Its primary runtime inputs are user-entered text and explicit external-link actions.

Controls include:

- parsing malformed/non-finite text before shared calculation;
- retaining authoritative range validation in `shared`;
- separating the under-18 path from adult reference calculators;
- keeping calculator/adult/theme/navigation state in process memory only;
- opening external URLs only on explicit user action;
- testing desktop adapter/parsing logic;
- building/testing desktop code on Linux, Windows, and macOS CI.

If desktop persistence/import is introduced later, its untrusted-input and storage threat model must be documented before release.

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

## Dependency policy

Prefer maintained dependencies from trusted sources. Dependency upgrades should pass the full applicable test/lint/build suite before merge. High-severity dependency-review findings block pull requests unless a documented false positive or accepted temporary exception exists.

Dependency changes affecting Compose Multiplatform must pass the three-OS Desktop workflow; shared Kotlin changes must also remain compatible with the configured Apple targets.

## Release security gates

Before tagging, the exact release commit should have successful:

- CI repository/docs audits, formatting, shared/desktop/Android tests, desktop JAR packaging, Android lint, APK and AAB assembly;
- Desktop Linux/Windows/macOS matrix;
- Android emulator instrumentation;
- Apple shared-core compilation;
- CodeQL analysis;
- dependency review where applicable;
- repository-history secret scanning.

Manual release security checks must also confirm:

- Android production signing remains outside source control;
- any future desktop native signing/notarization credentials remain outside source control;
- published desktop artifacts were manually launched on their target platform;
- no unexpected persistence/network behavior was introduced.

## Disclosure process

Maintainers will validate the report, determine severity, prepare a fix, add regression coverage where feasible, and publish release notes without unnecessary exploit detail. Reporters may be credited with permission.
