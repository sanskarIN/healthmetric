# Contributing to HealthMetric

Thank you for helping improve HealthMetric. Contributions should preserve the project's privacy-first, adult-only, educational, non-diagnostic approach.

## Development principles

- Keep calculation rules in the shared domain module when they are platform-neutral.
- Keep Android presentation, locale formatting, persistence, and platform integration concerns in `androidApp`.
- Keep shared-domain changes compatible with Android, JVM/Desktop, iOS device, and iOS simulator targets.
- Do not add appearance-shaming language, rankings, pressure-oriented goals, forced sign-in, advertising trackers, or unnecessary network dependencies.
- Never commit real personal health data. Use clearly fictional/example measurements in tests, screenshots, and issues.
- Treat backup files as untrusted input and preserve their documented size/schema/record/order bounds.
- Keep history opt-in and adult-use/onboarding state device-local; portable backups must not silently change consent or adult-only eligibility.
- Keep local/imported history canonical newest-first before applying retention; delete/undo must restore chronological position.
- Use collision-resistant identifiers for newly recorded local history and continue validating/deduplicating imported IDs.
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

Equivalent commands include formatting, shared JVM tests, Android JVM tests, release lint, debug APK assembly, unsigned release APK assembly, and unsigned release App Bundle assembly.

Also run repository/document integrity checks when documentation/configuration changes:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
```

For UI/persistence changes, run instrumentation tests on a device/emulator:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

The GitHub emulator workflow also produces the `android-release-screenshots` artifact from the real app. Release-critical UI changes must preserve or deliberately update that evidence journey.

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
- Update docs when setup, architecture, privacy, backup schema, behavior, release packaging/evidence, or release steps change.
- Include UI evidence for visual changes using fictional/example data only; prefer the CI-generated release screenshot artifact for release-critical review.
- Resolve formatting, test, lint, APK/AAB build, emulator, screenshot-evidence, Apple-target, documentation-integrity, and security failures before merge.
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
- canonical newest-first ordering before retention;
- per-record validation;
- duplicate-ID protection;
- collision-resistant IDs for new local entries;
- device-local history consent/adult-gate/onboarding state;
- explicit restore confirmation;
- regression coverage for migrations/compatibility.

## Navigation and accessibility changes

Secondary destinations must remain escapable with explicit and/or system back behavior. If a destination hides bottom navigation, add a regression test proving the expected return path.

Stable automation tags may be added for release-critical journeys, but they supplement rather than replace user-facing accessibility semantics.

## Release screenshot changes

If the required screenshot set or capture flow changes, update together:

- `ReleaseScreenshotCaptureTest`;
- `.github/workflows/android-instrumentation.yml`;
- `docs/assets/screenshots/README.md`;
- `scripts/check_repository.py`;
- release/testing documentation.

Never replace missing real-app evidence with fabricated/mock screenshots.

## Locale changes

Numeric presentation is an Android UI concern; shared arithmetic remains locale-neutral. Test dot- and comma-decimal behavior when touching input or display formatting.

## Security and privacy

Read [`SECURITY.md`](SECURITY.md) and [`PRIVACY.md`](PRIVACY.md). Report vulnerabilities privately instead of creating public exploit details.

## Conduct

Participation is governed by [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
