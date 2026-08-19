# Contributing to HealthMetric

Thank you for helping improve HealthMetric. Contributions should preserve the project's privacy-first, adult-only, educational, non-diagnostic approach.

## Development principles

- Keep calculation rules in the shared domain module when they are platform-neutral.
- Keep Android presentation, locale formatting, persistence, and platform integration concerns in `androidApp`.
- Keep shared-domain changes compatible with Android, JVM/Desktop, iOS device, and iOS simulator targets.
- Do not add appearance-shaming language, rankings, pressure-oriented goals, forced sign-in, advertising trackers, or unnecessary network dependencies.
- Never commit real personal health data. Use clearly fictional/example measurements in tests, screenshots, and issues.
- Treat backup files as untrusted input and preserve their documented size/schema/record bounds.
- Keep history opt-in and adult-use/onboarding state device-local; portable backups must not silently change consent or adult-only eligibility.
- Prefer small, atomic changes with tests and documentation.

## Local setup

See [`docs/setup.md`](docs/setup.md). The Android/JVM baseline toolchain is JDK 17, Gradle 8.13, Android SDK Platform 36, and Build Tools 35.0.0. Apple target compilation additionally requires macOS/Xcode.

## Quality checks

Run the complete non-device suite before opening a pull request:

```bash
bash scripts/verify.sh
```

or on Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Equivalent commands include formatting, shared JVM tests, Android JVM tests, release lint, debug assembly, and release assembly.

Also run repository/document integrity checks when documentation/configuration changes:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

For UI/persistence changes, run instrumentation tests on a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

For shared-domain changes on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Commit style

Use Conventional Commits where practical:

- `feat: ...`
- `fix: ...`
- `test: ...`
- `docs: ...`
- `refactor: ...`
- `perf: ...`
- `build: ...`
- `ci: ...`
- `chore: ...`

For local Git configuration, the project owner requested:

```bash
git config user.email "sanskarin@outlook.in"
```

Do not rewrite other contributors' identity metadata.

## Pull requests

- Keep each PR focused.
- Explain user-visible behavior changes.
- Add regression tests for bug fixes.
- Update docs when setup, architecture, privacy, backup schema, behavior, or release steps change.
- Include UI evidence for visual changes using fictional/example data only.
- Resolve formatting, test, lint, build, emulator, Apple-target, documentation-integrity, and security failures before merge.
- Complete the repository PR checklist rather than deleting privacy/safety items as “not applicable” without explanation.

## Calculation and evidence changes

Changes to adult reference ranges or explanatory health text require:

1. A documented authoritative source.
2. An updated source review date.
3. A clear versioned reference profile when thresholds/interpretation change.
4. Tests for every boundary.
5. Neutral wording that does not turn a population screening measure into a diagnosis or appearance goal.
6. Updates to [`docs/evidence.md`](docs/evidence.md), `CHANGELOG.md`, and release notes.

## Backup and persistence changes

Read [`docs/backup-format.md`](docs/backup-format.md) and ADR 0004 before changing portable data.

A change must retain or deliberately replace, with review:

- 1 MiB bounded IO;
- supported schema validation;
- bounded history retention;
- per-record validation;
- duplicate-ID protection;
- device-local history consent/adult-gate/onboarding state;
- explicit restore confirmation;
- regression coverage for migrations/compatibility.

## Locale changes

Numeric presentation is an Android UI concern; shared arithmetic remains locale-neutral. Test dot- and comma-decimal behavior when touching input or display formatting.

## Security and privacy

Read [`SECURITY.md`](SECURITY.md) and [`PRIVACY.md`](PRIVACY.md). Report vulnerabilities privately instead of creating public exploit details.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
