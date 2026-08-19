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
4. Run formatting/lint/tests/build.
5. Run connected UI tests.
6. Manually test onboarding, BMI, ratio, history disabled/enabled, export, restore, deletion, themes, About links, and large text.
7. Capture release screenshots with fictional/example data.
8. Confirm dependency/security checks are green.
9. Confirm no secrets/signing material are in Git history.

## Verification commands

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :shared:desktopTest
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleRelease
```

With a connected emulator/device:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

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
3. runs shared tests, ktlint, and release lint;
4. assembles the release build;
5. uploads the unsigned APK as a workflow artifact;
6. creates a GitHub Release with generated notes and the unsigned APK.

## Signing

The generated repository release APK is intentionally unsigned. Production distribution signing must happen through a protected environment using secrets that are never committed.

For Google Play, use a secure Play App Signing/release pipeline and prefer Android App Bundles when that distribution pipeline is introduced.

## Rollback

If a release has a blocker defect:

- mark the release as affected in release notes;
- fix on `main` with a regression test;
- publish a patch release;
- do not rewrite a published Git tag to hide history.

## Release notes content

Include:

- user-visible changes;
- privacy/data changes;
- reference/evidence changes;
- accessibility improvements;
- fixed defects;
- known limitations;
- upgrade/migration notes.
