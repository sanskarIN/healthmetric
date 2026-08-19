# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, and documentation checks pass.

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
3. Verify reference source metadata and adult-only copy.
4. Run formatting, shared tests, Android unit tests, release lint, debug assembly, and release assembly.
5. Run the connected Android instrumentation suite.
6. Confirm GitHub CI, Android instrumentation, CodeQL, dependency review, and secret scanning are green for the release commit/PR.
7. Manually test onboarding, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore, delete-all-data, themes, release link, About links, and large text.
8. Test one valid backup round trip and confirm malformed/unsupported files produce safe errors rather than partial uncontrolled writes.
9. Capture release screenshots with fictional/example data only.
10. Complete the manual TalkBack/accessibility checklist and record evidence.
11. Confirm no secrets/signing material are in Git history.
12. Configure production signing only in the protected distribution environment.

## Verification commands

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

The repository's `android-instrumentation.yml` workflow performs the connected test command on an API 35 emulator for pull requests and `main` pushes.

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
- restore rejects unsupported schema versions and oversized payloads;
- imported history never exceeds the selected supported retention limit;
- app manifest still has no Internet permission and keeps Android backup disabled.

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

The release workflow intentionally does not replace the pull-request emulator gate. A tag should be created only after the release commit has already passed connected instrumentation and security checks.

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

## Release notes content

Include:

- user-visible changes;
- privacy/data changes;
- reference/evidence changes;
- accessibility improvements;
- fixed defects;
- known limitations;
- backup/migration notes;
- verification status.
