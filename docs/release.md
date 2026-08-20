# Release Guide

## Release principles

A HealthMetric release is publishable only after the relevant platform's reproducible build, tests, lint/static checks, privacy review, accessibility review, evidence review, documentation, and signing/distribution controls pass.

No signing key, provisioning credential, store credential, or password belongs in the repository.

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features/platform additions;
- major: intentionally incompatible behavior/data contracts.

Keep Android `versionCode` monotonic and align user-facing platform versions when multiple clients are released from the same tag.

## Current distribution maturity

| Platform | Repository build status | Public distribution status |
|---|---|---|
| Android | Full debug/release APK + App Bundle build pipeline | Tagged GitHub release artifacts; production signing remains external |
| Windows | Native MSI packaging configured and CI-verified | Beta build artifact; not yet store-signed/published |
| macOS | Native DMG packaging configured and CI-verified | Beta build artifact; not yet notarized/store-published |
| Linux | Native DEB packaging configured and CI-verified | Beta build artifact; repository/packaging distribution not yet published |
| Web | JS + Wasm production bundles built/uploaded by CI | Hosting/security-header decision pending |
| iOS/iPadOS | SwiftUI host + shared framework simulator build in CI | Device signing/App Store/TestFlight distribution pending |

Do not describe a beta build artifact as a store-published or production-signed release.

## Universal pre-release checklist

1. Update `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md`.
2. Verify reference source metadata, review date, and adult-only copy.
3. Run `scripts/verify.sh` or `scripts/verify.ps1` from a clean checkout.
4. Confirm the shared `HealthMetricEngine` age boundary still rejects under-18 requests.
5. Confirm standard CI, cross-platform CI, native desktop packaging, Apple host build, CodeQL, dependency review, and secret scanning are green.
6. Review privacy documentation for every platform touched by the release.
7. Complete platform accessibility checks using fictional/example measurements only.
8. Confirm no secrets/signing material are in Git history.
9. Confirm documentation commands/links match the release commit.
10. Record known limitations rather than hiding unfinished platform parity.

## Local verification commands

### Main non-device suite

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows:

```powershell
.\scripts\verify.ps1
```

The scripts cover formatting, shared domain tests, JS/Wasm domain compilation, shared UI JVM/JS/Wasm compilation, desktop compilation, web production bundling, Android unit tests/lint, and Android debug/release assembly.

### Android connected tests

```bash
gradle :androidApp:connectedDebugAndroidTest
```

### Apple framework checks

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
gradle :sharedUI:linkDebugFrameworkIosSimulatorArm64 :sharedUI:linkDebugFrameworkIosArm64
```

### iOS generated host build

```bash
cd iosApp
xcodegen generate
cd ..
xcodebuild \
  -project iosApp/HealthMetricIOS.xcodeproj \
  -scheme HealthMetric \
  -configuration Debug \
  -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_ALLOWED=NO \
  build
```

### Desktop current-host package

```bash
gradle :desktopApp:packageDistributionForCurrentOS
```

### Web production bundles

```bash
gradle :webApp:wasmJsBrowserProductionWebpack
gradle :webApp:jsBrowserProductionWebpack
```

## Android release checklist

In addition to universal checks:

1. Run connected instrumentation on emulator/device.
2. Manually test onboarding, under-18 gate, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation/restore, delete-all-data, themes, release link, About links, and large text.
3. Test one valid backup round trip.
4. Confirm malformed/unsupported/oversized files produce safe errors without partial uncontrolled writes.
5. Confirm imported legacy fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
6. Check numeric input/display in at least one dot-decimal and one comma-decimal locale.
7. Capture release screenshots with fictional/example data.
8. Complete TalkBack/manual accessibility evidence.
9. Configure production signing only in the protected distribution environment.

### Android artifacts

Unsigned release APK:

```text
androidApp/build/outputs/apk/release/
```

Unsigned release App Bundle:

```text
androidApp/build/outputs/bundle/release/
```

The App Bundle is the preferred artifact for protected Google Play signing/distribution. Repository artifacts must not be represented as production-store-signed binaries.

## Desktop release checklist

CI builds host-native packages separately:

- Windows MSI;
- macOS DMG;
- Linux DEB.

Before public desktop distribution:

1. Confirm `desktop-packages.yml` is green on all three operating systems.
2. Install/test each generated package on a clean representative machine/VM.
3. Verify adult gate and both calculators.
4. Verify keyboard focus/navigation and screen-reader behavior.
5. Verify window resizing and HiDPI/scaling behavior.
6. Confirm there is no unexpected persistence/network telemetry.
7. Configure platform-appropriate code signing where required.
8. For macOS public distribution, complete signing/notarization requirements outside source control.
9. Add uninstall/update documentation for the chosen distribution channel.

## Web release checklist

CI produces JavaScript and Wasm production artifacts.

Before public hosting:

1. Verify both production bundles load correctly.
2. Test adult gating/calculation in representative current browsers.
3. Choose Wasm primary vs JavaScript compatibility routing based on tested browser support.
4. Verify keyboard/screen-reader behavior and responsive layouts.
5. Choose a hosting provider/process.
6. Configure HTTPS and suitable security headers in the hosting layer.
7. Document host/CDN access-log and privacy behavior separately from calculator runtime behavior.
8. Do not add analytics/advertising by default.
9. Add cache/version rollback rules for static assets.

## iOS/iPadOS release checklist

Before TestFlight/App Store or other signed distribution:

1. Regenerate the Xcode project from `iosApp/project.yml`.
2. Confirm Apple CI framework and simulator-host builds are green.
3. Build/run on a current iPhone/iPad simulator.
4. Build/run on a physical configured device.
5. Verify adult gate and calculator flows.
6. Verify VoiceOver and Dynamic Type behavior.
7. Configure Apple signing/team/provisioning outside Git source.
8. Create store metadata/screenshots using fictional/example measurements.
9. Review privacy disclosures against the actual signed app.
10. Archive/upload only from a protected Apple signing environment.

## Android privacy/data checks

Before an Android release verify:

- fresh-install history is disabled;
- selecting 50/100/250/500 retention trims history as documented;
- raw weight/height/waist fields do not appear in persisted history/export;
- individual deletion can be undone without enabling future history saving;
- erase-all requires confirmation;
- delete-all returns to onboarding/privacy defaults;
- file backup uses Android document picker;
- share backup uses explicit chooser;
- restore asks for confirmation before mutation;
- restore rejects unsupported schema versions and oversized payloads;
- malformed history records are skipped independently;
- duplicate IDs cannot become duplicate Compose list keys;
- imported history never exceeds selected supported retention;
- portable JSON omits `historyEnabled`, `adultUseConfirmed`, and `onboardingComplete`;
- legacy fields cannot change current consent/safety state;
- manifest still has no Internet permission and keeps Android backup disabled.

The authoritative schema contract is [`backup-format.md`](backup-format.md).

## Locale checks

Android release verification must include:

- dot-decimal input/display under an English locale;
- comma-decimal input/display under a comma-decimal locale;
- intended BMI and ratio precision;
- history/chart accessibility summaries matching visible formatting.

Desktop/web/iOS currently accept practical dot/comma entry through the common UI. Rich locale display parity is future work and should not be claimed until implemented/tested.

## Tagging

Create an annotated tag only after the intended release scope is ready:

```bash
git tag -a v0.1.0 -m "HealthMetric v0.1.0"
git push origin v0.1.0
```

Tags matching `v*` trigger `.github/workflows/release.yml`.

## Current automated tagged release workflow

The tagged workflow is Android-focused. It:

1. sets up JDK 17 and Gradle 8.13;
2. installs Android SDK packages;
3. runs shared tests, Android unit tests, ktlint, and release lint;
4. assembles unsigned Android release APK and App Bundle;
5. uploads them as workflow artifacts;
6. creates a GitHub Release with generated notes and unsigned artifacts.

Desktop/web/iOS pull-request/main workflows remain independent validation gates. A tag must be created only after the same release commit has passed them when those platforms are part of the claimed release scope.

## Signing and secrets

Never commit:

- Android keystores/passwords;
- Apple certificates/private keys;
- provisioning profiles containing protected material;
- store/API credentials;
- service-account secrets.

Use protected distribution environments and official platform signing processes.

## Rollback

If a release has a blocker defect:

- mark it affected in release notes;
- fix `main` with a regression test;
- publish a patch release;
- do not rewrite a published Git tag to hide history.

For web, keep enough prior static artifacts/configuration to roll back a bad deployment safely.

If backup behavior changes, retain supported compatibility or explicitly document migration before shipping.

If evidence/reference interpretation changes, ship an explicit versioned profile change rather than silently changing a published interpretation.

## Release notes content

Include:

- user-visible changes;
- affected platforms;
- privacy/data changes;
- portable backup/migration changes;
- reference/evidence changes and review date where applicable;
- accessibility improvements;
- build/package changes;
- fixed defects;
- known limitations;
- exact CI/manual verification status;
- signing/distribution status.
