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
4. Run formatting, shared tests, Android unit tests, release lint, debug assembly, and release assembly.
5. Run the connected Android instrumentation suite.
6. Compile the iOS shared-core targets on macOS.
7. Confirm GitHub CI, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scanning are green for the release commit/PR.
8. Manually test onboarding, under-18 gate, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation, restore, delete-all-data, themes, release link, About links, and large text.
9. Test one valid backup round trip and confirm malformed/unsupported/oversized files produce safe errors rather than partial uncontrolled writes.
10. Confirm imported legacy fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
11. Check numeric input/display in at least one dot-decimal and one comma-decimal locale.
12. Capture release screenshots with fictional/example data only.
13. Complete the manual TalkBack/accessibility checklist and record evidence.
14. Confirm no secrets/signing material are in Git history.
15. Configure production signing only in the protected distribution environment.

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
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
```

With a connected emulator/device:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

The repository's `android-instrumentation.yml` workflow performs connected Android tests on an API 35 emulator. `apple-shared.yml` runs shared JVM tests and compiles both configured iOS targets on macOS.

## Release data/privacy checks

Before tagging, verify:

- fresh install history is disabled;
- selecting 50/100/250/500 retention trims history as documented;
- raw weight, height, and waist fields do not appear in persisted history/export;
- individual deletion can be undone without enabling future history saving;
- erase-all requires confirmation;
- delete-all returns to onboarding/privacy defaults;
- file backup uses Android's document picker;
- share backup uses an explicit chooser;
- restore asks for confirmation before mutation;
- restore rejects unsupported schema versions and oversized payloads;
- malformed history records are skipped independently;
- duplicate IDs cannot become duplicate Compose list keys;
- imported history never exceeds the selected supported retention limit;
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
4. assembles the unsigned release build;
5. uploads the unsigned APK as a workflow artifact;
6. creates a GitHub Release with generated notes and the unsigned APK.

The tagged release workflow intentionally does not replace pull-request emulator, Apple-target, and security gates. A tag should be created only after the release commit has already passed those checks.

## Signing

The generated repository release APK is intentionally unsigned. Production distribution signing must happen through a protected environment using secrets that are never committed.

For Google Play, use a secure Play App Signing/release pipeline and prefer Android App Bundles when that distribution pipeline is introduced.

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
- fixed defects;
- known limitations;
- verification status.
