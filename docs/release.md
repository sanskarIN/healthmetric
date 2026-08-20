# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, evidence, documentation, and supported-platform checks pass.

The repository never stores Android signing keys, Apple signing identities, signing passwords, private provisioning material, or store credentials.

## Current release line

The cross-platform application work is aligned to version **2.0.12**.

Version locations include:

- Android `versionName` / `versionCode` in `androidApp/build.gradle.kts`;
- desktop package version in `composeApp/build.gradle.kts`;
- iOS marketing/bundle version metadata in `iosApp`.

Keep these values aligned when preparing a tagged release.

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features;
- major: intentionally incompatible behavior/data contracts.

Android `versionCode` must remain monotonic even when `versionName` follows SemVer.

## Pre-release checklist

1. Update `CHANGELOG.md`.
2. Update `ROADMAP.md` and `what_changed.md`.
3. Verify all platform/version metadata is aligned.
4. Verify reference source metadata, `reviewedOnIsoDate`, and adult-only/non-diagnostic copy.
5. Run shared/Compose/Android formatting checks and shared tests.
6. Run Android JVM unit tests, release lint, debug/release APK assembly, and release App Bundle assembly.
7. Run connected Android instrumentation tests.
8. Build JS and Wasm production browser distributions.
9. Build the compatibility browser distribution and open it in representative browsers.
10. Build the native desktop package on Windows, macOS, and Linux.
11. Compile the iOS device/simulator frameworks on macOS.
12. Build the native iOS simulator application through Xcode.
13. Confirm CI, Cross-platform, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scan are green for the release commit/PR.
14. Manually test the shared client adult gate, metric/imperial BMI, waist-to-height flows, validation, and window/mobile resizing.
15. Manually test Android onboarding, under-18 gate, history disabled/enabled, retention changes, delete/undo, erase-all, backup/share/restore, themes, About, update links, and large text.
16. Test one valid Android backup round trip and malformed/unsupported/oversized backup errors.
17. Confirm imported legacy fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
18. Check Android numeric input/display in dot-decimal and comma-decimal locales.
19. Capture release screenshots using fictional/example data only.
20. Complete platform accessibility/manual checks.
21. Confirm no secrets/signing material exist in Git history.
22. Configure production store signing only in protected distribution environments.

## Complete local verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

The scripts verify shared tests, formatting, desktop compilation, JS/Wasm production builds, compatibility browser output, Android tests/lint/packages, and the iOS simulator framework when the Unix script is run on macOS.

Android instrumentation:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Native desktop package for the current host:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

On macOS, also verify the iOS host:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

See [`testing.md`](testing.md) and [`cross-platform.md`](cross-platform.md) for the full matrix.

## Android release artifacts

The tagged workflow builds both Android package formats without embedding production signing secrets:

- `HealthMetric-<tag>-android-unsigned.apk`;
- `HealthMetric-<tag>-android-unsigned.aab`.

The AAB is appropriate as an input to a protected Google Play signing/distribution workflow. The unsigned APK is useful for release-candidate inspection and controlled testing.

Neither should be represented as a production-store-signed binary until it has passed the appropriate protected signing process.

## Windows release artifact

The Windows release job runs on `windows-latest` and executes:

```text
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:packageDistributionForCurrentOS
```

The configured native package is collected as:

```text
HealthMetric-<tag>-windows.msi
```

## macOS release artifact

The macOS release job runs on `macos-latest` and produces:

```text
HealthMetric-<tag>-macos.dmg
```

The repository build produces the package, but production distribution may additionally require Apple application signing/notarization outside the source repository.

## Linux release artifact

The Linux release job runs on `ubuntu-latest` and produces:

```text
HealthMetric-<tag>-linux.deb
```

## Web release artifact

The browser release job builds:

```bash
gradle :composeApp:composeCompatibilityBrowserDistribution
```

The generated distribution is archived as:

```text
HealthMetric-<tag>-web.zip
```

The ZIP is intended for static-host deployment. It contains the built browser distribution rather than source-only web files.

## iOS/iPadOS release artifact

The release workflow verifies the native iOS simulator application and builds a release iOS ARM64 Kotlin framework.

It publishes:

```text
HealthMetric-<tag>-ios-framework.zip
```

This ZIP is a **developer integration artifact**, not a signed App Store IPA. A production iPhone/iPad application must be archived and signed in a protected Apple/Xcode distribution environment using the correct development team, bundle identity, certificates, and provisioning configuration.

This separation is intentional: private Apple signing material must never be committed merely to make CI emit an installable IPA.

## GitHub release workflow

Pushing a tag matching `v*` triggers `.github/workflows/release.yml`.

The workflow has independent jobs for:

- Android;
- Web;
- Windows/macOS/Linux desktop matrix;
- iOS developer framework + Xcode simulator verification;
- final GitHub Release publication.

Each build job uploads an Actions artifact. The final `publish` job downloads all `release-*` artifacts into one directory and requires at least seven files before creating/updating the GitHub Release.

Expected release assets:

1. Android unsigned APK.
2. Android unsigned AAB.
3. Windows MSI.
4. macOS DMG.
5. Linux DEB.
6. Web ZIP.
7. iOS developer framework ZIP.

The final job uses GitHub's release CLI. On a clean tag it creates a release with generated notes; on a rerun it uploads/replaces the release assets without requiring tag rewriting.

## Tagging 2.0.12

Create the tag only after the exact release commit has passed required checks:

```bash
git tag -a v2.0.12 -m "HealthMetric v2.0.12"
git push origin v2.0.12
```

Do not move or rewrite a published release tag to hide defects. Ship a new patch version instead.

## Android data/privacy release checks

Before tagging, verify:

- fresh-install history is disabled;
- 50/100/250/500 retention behaves as documented;
- raw weight/height/waist input fields do not appear in persisted history/export;
- individual deletion can be undone without enabling future saving;
- erase-all requires confirmation;
- delete-all returns to onboarding/privacy defaults;
- file backup uses Android's document picker;
- share backup uses an explicit chooser;
- restore asks for confirmation before mutation;
- unsupported/oversized payloads are rejected;
- malformed history records are skipped independently;
- duplicate IDs cannot become duplicate list keys;
- restored history is bounded;
- portable JSON omits `historyEnabled`, `adultUseConfirmed`, and `onboardingComplete`;
- legacy documents cannot alter those current device-local choices;
- Android manifest still has no Internet permission and keeps Android backup disabled.

See [`backup-format.md`](backup-format.md).

## Shared-client safety checks

Before tagging, verify every shared client:

- requires adult-use confirmation before adult reference calculators;
- uses the `shared` calculator domain rather than platform-local formulas;
- rejects invalid/out-of-range inputs through the same domain validation;
- describes results as educational screening information, not diagnoses;
- does not present appearance scores, ideal-body rankings, or pressure-oriented targets;
- performs calculator arithmetic locally without requiring a remote health API.

## Locale checks

For the mature Android client, verify at least:

- dot-decimal input/display in an English locale;
- comma-decimal input/display in a comma-decimal locale;
- BMI one-decimal display precision;
- waist-to-height two-decimal display precision;
- history/chart accessibility summaries match visible locale formatting.

For shared clients, verify practical dot/comma parsing and correct numeric results in representative environments.

## Signing and store distribution

### Android

Use Play App Signing or another protected signing pipeline. Never add keystores, passwords, signing certificates, or service-account credentials to the repository.

### Apple

Use Xcode/Apple protected distribution signing for device/App Store releases. Never add private certificates, signing keys, provisioning secrets, or account credentials to the repository.

### Desktop

The repository generates native packages. Publisher signing/notarization, where required by the destination ecosystem, should be performed by a protected release process.

### Web

Deploy the generated static assets through the chosen hosting/CDN pipeline. Hosting credentials belong in the hosting platform's protected secret store, not source control.

## Rollback

If a release has a blocker defect:

- mark the release as affected in release notes;
- fix on `main` with regression coverage;
- publish a patch release;
- do not rewrite a published tag.

If backup behavior changes, retain supported schema compatibility or explicitly document a migration.

If reference/evidence interpretation changes, ship an explicit versioned profile change rather than silently mutating a published interpretation.

## Release notes content

Include:

- user-visible changes;
- supported-platform changes;
- feature-parity notes;
- privacy/data changes;
- backup/migration changes;
- reference/evidence changes and review date where applicable;
- accessibility improvements;
- desktop/web/iOS packaging changes;
- Android APK/AAB packaging changes;
- fixed defects;
- known limitations;
- CI/verification status.
