# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, evidence, and documentation checks pass.

The repository never stores Android signing keys or signing passwords.

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features;
- major: intentionally incompatible behavior/data contracts.

Keep `androidApp` `versionCode` monotonic and update `versionName` to the release version.

## Pre-release checklist

1. Update `CHANGELOG.md`.
2. Update `ROADMAP.md` and `what_changed.md`.
3. Verify reference source metadata, `reviewedOnIsoDate`, and adult-only copy.
4. Run repository/link audits, formatting, shared tests, Android unit tests, release lint, debug assembly, release APK assembly, and release App Bundle assembly.
5. Run the connected Android instrumentation suite.
6. Confirm the instrumentation run produced all eight PNG files in the `android-release-screenshots` artifact and visually/privacy-review them.
7. Compile the iOS shared-core targets on macOS.
8. Confirm GitHub CI, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scanning are green for the exact release commit/PR.
9. Manually test onboarding, under-18 gate, BMI, ratio, history disabled/enabled, retention changes, chronological history ordering, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation, restore, delete-all-data, About return navigation, themes, release link, About links, and large text.
10. Test one valid backup round trip and confirm malformed/unsupported/oversized files produce safe errors rather than partial uncontrolled writes.
11. Confirm imported legacy fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
12. Confirm imported history is normalized newest-first before retention is applied and an older deleted entry returns to its chronological position after Undo.
13. Check numeric input/display in at least one dot-decimal and one comma-decimal locale.
14. Complete the manual TalkBack/accessibility checklist and record evidence.
15. Confirm no secrets/signing material are in Git history.
16. Configure production signing only in the protected distribution environment.

## Verification commands

Complete non-device suite:

```bash
bash scripts/verify.sh
```

or on Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Equivalent commands:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
gradle :androidApp:bundleRelease
```

With a connected emulator/device:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

The repository's `android-instrumentation.yml` workflow performs connected Android tests on an API 35 Pixel 7 emulator and uploads the required real-app screenshot evidence. `apple-shared.yml` runs shared JVM tests and compiles both configured iOS targets on macOS.

## Android release artifacts

The repository builds both Android release package formats without embedding distribution signing secrets:

- unsigned release APK: `androidApp/build/outputs/apk/release/`;
- unsigned release App Bundle: `androidApp/build/outputs/bundle/release/`.

The App Bundle is the preferred artifact for a future Google Play protected signing/distribution pipeline. The APK remains useful for release-candidate inspection and controlled testing.

Neither repository artifact should be represented as a production-store-signed binary. Production signing must be performed through a protected process outside Git source.

## Release screenshot evidence

The connected instrumentation suite captures:

- `01-onboarding.png`;
- `02-bmi-metric.png`;
- `03-bmi-result.png`;
- `04-waist-ratio.png`;
- `05-history.png`;
- `06-settings.png`;
- `07-about.png`;
- `08-dark-theme.png`.

GitHub Actions pulls the files from app-scoped external storage and uploads the `android-release-screenshots` artifact. Missing files fail the upload step. Before publication, inspect every screenshot for clipping, overlays, accidental personal data, readability, neutral adult-only wording, and visual consistency. See [`assets/screenshots/README.md`](assets/screenshots/README.md).

## Release data/privacy checks

Before tagging, verify:

- fresh install history is disabled;
- selecting 50/100/250/500 retention trims history as documented;
- raw weight, height, and waist fields do not appear in persisted history/export;
- new local history IDs use collision-resistant UUID values;
- individual deletion can be undone without enabling future history saving;
- Undo restores chronological position rather than moving an older entry to the top;
- erase-all requires confirmation;
- delete-all returns to onboarding/privacy defaults;
- file backup uses Android's document picker;
- share backup uses an explicit chooser;
- restore asks for confirmation before mutation;
- restore rejects unsupported schema versions and oversized payloads;
- malformed history records are skipped independently;
- duplicate IDs cannot become duplicate Compose list keys;
- imported history is ordered newest-first and never exceeds the selected supported retention limit;
- portable backup JSON omits `historyEnabled`, `adultUseConfirmed`, and `onboardingComplete`;
- legacy backups containing those fields cannot change current device-local consent/safety state;
- app manifest still has no Internet permission and keeps Android backup disabled.

The authoritative schema contract is [`backup-format.md`](backup-format.md).

## Release locale checks

At minimum, verify:

- dot-decimal input/display under an English locale;
- comma-decimal input/display under a comma-decimal locale;
- BMI displays at the intended one-decimal precision;
- waist-to-height values display at the intended two-decimal precision;
- history and chart accessibility summaries match the visible locale formatting.

## Tagging

Create an annotated tag only after the release commit is ready:

```bash
git tag -a v0.1.0 -m "HealthMetric v0.1.0"
git push origin v0.1.0
```

Tags matching `v*` trigger `.github/workflows/release.yml`.

## Automated release workflow

The workflow:

1. sets up JDK 17 and Gradle 8.13;
2. installs Android SDK packages;
3. runs shared tests, Android unit tests, ktlint, and release lint;
4. assembles the unsigned release APK and App Bundle;
5. uploads the unsigned APK and App Bundle as workflow artifacts;
6. creates a GitHub Release with generated notes and both unsigned artifacts.

The tagged release workflow intentionally does not replace pull-request emulator, screenshot-evidence, Apple-target, and security gates. A tag should be created only after the exact release commit has already passed those checks.

## Signing

The generated repository release APK and App Bundle are intentionally unsigned. Production distribution signing must happen through a protected environment using secrets that are never committed.

For Google Play, use Play App Signing or another protected release pipeline for the App Bundle. Do not add keystores, passwords, signing certificates, or service-account credentials to the repository.

## Rollback

If a release has a blocker defect:

- mark the release as affected in release notes;
- fix on `main` with a regression test;
- publish a patch release;
- do not rewrite a published Git tag to hide history.

If a release changes backup behavior, retain compatibility with supported schema versions or explicitly document a migration before shipping.

If evidence/reference interpretation changes, ship it as an explicit versioned profile change rather than silently mutating a published interpretation.

## Release notes content

Include:

- user-visible changes;
- privacy/data changes;
- portable backup/migration changes;
- reference/evidence changes and source review date where applicable;
- accessibility improvements;
- platform/shared-core target changes;
- Android APK/App Bundle packaging changes;
- screenshot-evidence status;
- fixed defects;
- known limitations;
- verification status.
