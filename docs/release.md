# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, evidence, documentation, and platform checks pass.

The repository never stores Android signing keys, signing passwords, production credentials, or private certificates.

HealthMetric currently has:

- an Android application;
- a Compose Multiplatform desktop client for JVM environments;
- a Kotlin Multiplatform shared calculation core with Android, JVM/Desktop, iOS device, and iOS simulator targets.

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features;
- major: intentionally incompatible behavior/data contracts.

For a release:

- keep Android `versionCode` monotonic;
- update Android `versionName` to the release version;
- update the desktop module version/package version to the same public release version;
- update release notes and changelog consistently.

## Pre-release checklist

1. Update `CHANGELOG.md`.
2. Update `ROADMAP.md` and `what_changed.md`.
3. Verify reference source metadata, `reviewedOnIsoDate`, and adult-only copy.
4. Run repository invariants and internal Markdown-link checks.
5. Run shared, Android, and desktop formatting/tests.
6. Run Android release lint, debug assembly, unsigned release APK assembly, and unsigned release App Bundle assembly.
7. Package a desktop runnable JAR on each supported desktop operating-system family that will be distributed.
8. Run connected Android instrumentation on an emulator/device.
9. Compile the iOS shared-core targets on macOS.
10. Confirm GitHub CI, Desktop, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scanning are green for the exact release PR/commit.
11. Manually test Android onboarding, under-18 gate, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation, restore, delete-all-data, themes, release link, About links, and large text.
12. Test one valid Android backup round trip and confirm malformed/unsupported/oversized files produce safe errors rather than uncontrolled writes.
13. Confirm imported legacy Android fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
14. Check Android numeric input/display in at least one dot-decimal and one comma-decimal locale.
15. Manually test desktop adult gate, under-18 path, metric/imperial BMI, metric/imperial waist-to-height, theme toggle, About/evidence links, and process restart.
16. Confirm desktop measurements/results/adult choice/theme state are not retained after closing/reopening the application.
17. Manually launch desktop artifacts on every platform being published and verify keyboard focus, display scaling, screen-reader naming where available, and external-link behavior.
18. Capture release screenshots with fictional/example data only.
19. Complete the Android TalkBack/accessibility checklist and desktop accessibility checklist and record evidence.
20. Confirm no secrets/signing material are in Git history.
21. Configure production Android signing only in a protected distribution environment.
22. Create the release tag only after all blockers above are closed.

## Verification commands

Complete non-device suite on Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Equivalent major commands:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
gradle :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
gradle :shared:desktopTest
gradle :desktopApp:test
gradle :desktopApp:packageUberJarForCurrentOS
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
gradle :androidApp:assembleRelease
gradle :androidApp:bundleRelease
```

With a connected Android emulator/device:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Automated workflow gates

Before tagging, the exact release commit should already have passed:

- `CI` — repository/docs audits, shared/Android/desktop formatting, shared tests, desktop tests/JAR packaging, Android JVM tests/lint/build/APK/AAB;
- `Desktop` — desktop formatting/tests/runnable-JAR packaging on Linux, Windows, and macOS;
- `Android instrumentation` — connected tests on the configured API 35 emulator;
- `Apple shared core` — shared JVM tests plus iOS device/simulator compilation on macOS;
- `CodeQL`;
- `Dependency Review` where applicable;
- `Secret Scan`.

The tagged release workflow does not replace these pull-request/main gates.

## Android release artifacts

Repository builds create:

- unsigned release APK under `androidApp/build/outputs/apk/release/`;
- unsigned release App Bundle under `androidApp/build/outputs/bundle/release/`.

The App Bundle is the preferred input for a protected Google Play signing/distribution pipeline. The APK remains useful for controlled release-candidate inspection/testing.

Neither repository artifact should be represented as a production-store-signed binary. Production signing must happen outside Git source using protected credentials.

## Desktop release artifacts

The desktop module can build a current-operating-system runnable JAR with:

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

The cross-platform Desktop workflow verifies this on:

- Linux;
- Windows;
- macOS.

The module also declares native packaging formats for:

- Debian-compatible Linux DEB;
- Windows MSI;
- macOS DMG.

Native installers are operating-system-specific and require manual platform verification before publication. A green runnable-JAR workflow does not by itself certify installer UX, code signing/notarization, OS reputation prompts, or assistive-technology behavior.

## Android release data/privacy checks

Before tagging, verify:

- fresh install history is disabled;
- selecting 50/100/250/500 retention trims history as documented;
- raw weight, height, and waist fields do not appear in persisted history/export;
- individual deletion can be undone without enabling future history saving;
- restored/undone entries maintain newest-first chronology;
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

## Desktop release privacy checks

Before publishing a desktop artifact, verify:

- adult calculators are unavailable until the adult option is explicitly selected;
- the under-18 path does not reveal adult reference results;
- calculator inputs/results reset after application restart;
- adult-use selection resets after application restart;
- theme/navigation selection resets after application restart;
- no desktop measurement/history file or preferences store is introduced unintentionally;
- external evidence/repository/funding URLs open only after a button press;
- shared calculation/reference rules remain sourced from `shared` rather than copied into desktop UI code.

See [`desktop.md`](desktop.md) and ADR 0005.

## Locale/input checks

Android release checks should include at least one dot-decimal and one comma-decimal locale and confirm displayed precision/history/chart summaries remain consistent.

Desktop input accepts dot or comma decimals through its presentation parser. Verify both forms on at least one release platform. Shared arithmetic remains locale-independent.

## Tagging

Create an annotated tag only after the release commit is ready:

```bash
git tag -a v0.1.0 -m "HealthMetric v0.1.0"
git push origin v0.1.0
```

Tags matching `v*` trigger `.github/workflows/release.yml`.

## Automated tagged release workflow

The tagged workflow separates build verification from publication.

### Android release job

- checks repository invariants and Markdown links;
- sets up JDK/Gradle/Android SDK;
- runs shared tests/formatting plus Android formatting, unit tests, and release lint;
- creates unsigned APK and AAB;
- stages versioned Android assets;
- uploads them as a workflow artifact.

### Desktop release matrix

On Linux, Windows, and macOS it:

- runs desktop formatting and tests;
- packages the current-OS runnable JAR;
- stages a versioned platform-specific JAR;
- uploads each JAR as a workflow artifact.

### Publish job

The publish job starts only after Android and all desktop matrix jobs succeed. It downloads the verified artifacts, requires the expected APK/AAB/Linux JAR/Windows JAR/macOS JAR set to be present and non-empty, then creates one GitHub Release with generated notes and all verified assets.

## Signing and platform trust

### Android

Production Android signing must happen through a protected environment using secrets that are never committed. For Google Play, use Play App Signing or another protected release pipeline for the App Bundle.

### Desktop

The current repository release workflow publishes runnable JARs, not signed/notarized native installers. If native installers are published later, document and protect platform signing/notarization credentials outside source control and add a dedicated reviewed release process.

## Rollback

If a release has a blocker defect:

- mark the release as affected in release notes;
- fix on `main` with regression coverage;
- publish a patch release;
- do not rewrite a published Git tag to hide history.

If Android backup behavior changes, retain compatibility with supported schema versions or explicitly document a migration before shipping.

If evidence/reference interpretation changes, ship it as an explicit versioned profile change rather than silently mutating a published interpretation.

If desktop persistence is introduced, update ADR 0005 or supersede it with a reviewed persistence decision before release.

## Release notes content

Include:

- user-visible changes by platform;
- privacy/data changes;
- portable backup/migration changes;
- reference/evidence changes and source review date where applicable;
- accessibility improvements;
- platform/shared-core target changes;
- Android APK/App Bundle packaging changes;
- desktop artifact changes;
- fixed defects and regression coverage;
- known limitations;
- exact verification status.
