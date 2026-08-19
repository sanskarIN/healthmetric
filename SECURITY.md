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
- no account or authentication system is required;
- no advertising or analytics tracker SDK is included;
- cleartext network traffic is disabled in the Android manifest;
- Android application backup is disabled by default;
- local history is optional and can be deleted;
- export/restore is explicit and user-initiated;
- restored JSON is schema-checked and history is bounded;
- measurement values are not intentionally written to logs;
- signing keys and secrets are excluded from source control;
- CodeQL and dependency review run in GitHub Actions;
- Dependabot monitors Gradle and GitHub Actions dependencies.

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

## Disclosure process

Maintainers will validate the report, determine severity, prepare a fix, add regression coverage where feasible, and publish release notes without unnecessary exploit detail. Reporters may be credited with permission.
