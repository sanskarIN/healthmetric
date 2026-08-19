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
2. affected component;
3. impact description;
4. minimal reproduction using fictional data;
5. suggested mitigation if known.

Do not send real health records, credentials, tokens, signing material, or other private user data.

## Security design

HealthMetric intentionally minimizes attack surface:

- core calculators work offline;
- the Android manifest requests no Internet permission;
- no account or authentication system is required;
- no advertising or analytics tracker SDK is included;
- cleartext network traffic is disabled in the Android manifest;
- Android application backup is disabled;
- local history is disabled by default, explicitly opt-in, bounded, selectively deletable, and fully erasable;
- new local history records use collision-resistant UUID identifiers;
- history is normalized newest-first before retention is applied, including import and delete/undo restoration;
- export/restore is explicit and user-initiated;
- restore requires confirmation before portable local data is replaced;
- backup reads/writes are limited to 1 MiB;
- restored JSON is schema-checked, record-validated, deduplicated, chronologically normalized, and history-bounded;
- portable backup restore cannot change current history opt-in, adult-use confirmation, or onboarding safety state;
- raw weight, height, and waist inputs are not persisted in history;
- backup contents and measurement values are not intentionally written to logs;
- signing keys and secrets are excluded from source control;
- CodeQL, dependency review, full-history secret scanning, Android instrumentation, repository invariants, and standard build/test checks run in GitHub Actions;
- Dependabot monitors Gradle and GitHub Actions dependencies.

See [`docs/backup-format.md`](docs/backup-format.md) and [`docs/adr/0004-bounded-user-controlled-local-data.md`](docs/adr/0004-bounded-user-controlled-local-data.md) for the backup trust boundary.

## Imported-data threat model

A selected backup document is untrusted input even though the user explicitly chose it.

HealthMetric therefore:

- bounds the UTF-8 payload before JSON parsing;
- accepts only the supported top-level schema version;
- normalizes retention/theme preferences to supported values;
- validates every history record independently;
- rejects invalid/blank IDs, negative timestamps, non-finite numeric values, and unknown calculator kinds;
- caps record field lengths;
- deduplicates record IDs before they reach Compose list keys;
- sorts accepted records by timestamp descending before retention is applied;
- caps final restored history;
- keeps consent/safety state device-local;
- performs the DataStore mutation only after user confirmation.

A future schema must retain equivalent protections or record the reviewed replacement in an ADR.

## External intents

GitHub/release/funding/email links and backup share destinations are opened only after explicit user actions. HealthMetric does not guarantee the privacy/security behavior of the selected external application or destination.

## Secret handling

Never commit:

- API keys or access tokens;
- signing keystores/passwords;
- private production endpoints;
- personal health information;
- generated credentials;
- private certificates/keys.

`.env.example` contains placeholders/documentation only. Store signing material outside the repository and inject it through protected local/CI secret mechanisms.

## Dependency policy

Prefer maintained dependencies from trusted sources. Dependency upgrades should pass the full test/lint/build suite before merge. High-severity dependency-review findings block pull requests unless a documented false positive or accepted temporary exception exists.

## Release security gates

Before tagging, the exact release commit should have successful:

- repository invariant and internal Markdown-link audits;
- formatting, shared JVM tests, Android unit tests, and Android release lint;
- debug APK, unsigned release APK, and unsigned release App Bundle assembly with expected artifacts present;
- Android emulator instrumentation, including the required real-app release screenshot artifact;
- Apple shared-core compilation;
- CodeQL analysis;
- dependency review where applicable;
- repository-history secret scanning.

The CI-generated screenshot artifact must receive human privacy/visual review before permanent publication, and physical-device/manual accessibility checks remain release gates even though they are not automated security scans.

Production signing must remain outside source control. Repository-produced APK/AAB artifacts are intentionally unsigned and must never be represented as store-signed production binaries.

## Disclosure process

Maintainers will validate the report, determine severity, prepare a fix, add regression coverage where feasible, and publish release notes without unnecessary exploit detail. Reporters may be credited with permission.
