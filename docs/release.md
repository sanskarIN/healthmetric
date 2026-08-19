# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, evidence, documentation, and platform checks pass.

The repository never stores Android signing keys, signing passwords, production credentials, or private certificates.

HealthMetric currently has:

- an Android application;
- a Compose Multiplatform desktop client for JVM environments;
- a Kotlin Multiplatform shared calculation core with Android, JVM/Desktop, iOS device, and iOS simulator targets.

A green build is necessary but not sufficient. Physical-device testing, assistive-technology review, screenshot approval, target-host smoke testing, and protected signing/notarization remain human/external gates where documented.

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features;
- major: intentionally incompatible behavior/data contracts.

For a release:

- keep Android `versionCode` monotonic;
- update Android `versionName` to the public release version;
- update the desktop project version to the public release version;
- require the release tag to use stable `vMAJOR.MINOR.PATCH` form and match both Android `versionName` and the desktop project version;
- create the release tag only on the current `main` commit; the tagged workflow fails closed when the tag targets any other commit;
- keep host-native package metadata valid for the target packaging tool while documenting any required platform-specific mapping;
- for the planned public desktop `0.1.0` release, the macOS DMG uses native package metadata `1.0.0` because the DMG packaging tool requires a positive major component; this does not change the public HealthMetric version/tag;
- update release notes and changelog consistently.

## Pre-release checklist

1. Update `CHANGELOG.md`.
2. Update `ROADMAP.md` and `what_changed.md`.
3. Confirm [`documentation-map.md`](documentation-map.md) still identifies the canonical current contracts.
4. Confirm [`repository-file-reference.md`](repository-file-reference.md) contains every exact `git ls-files` path and no tracked file was added without responsibility documentation.
5. Verify reference source metadata, `reviewedOnIsoDate`, and adult-only copy.
6. Run repository invariants, repository-tooling regression tests, and internal Markdown-link checks.
7. Run shared, Android, and desktop formatting/tests.
8. Run Android release lint, debug assembly, unsigned release APK assembly, and unsigned release App Bundle assembly.
9. Package the desktop runnable JAR and native installer on every desktop operating-system family being published: DEB on Linux, MSI on Windows, DMG on macOS.
10. Run connected Android instrumentation on an emulator/device and inspect the generated screenshot evidence set.
11. Compile the iOS shared-core targets on macOS.
12. Confirm GitHub CI, Desktop, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scanning are green for the **exact release PR/commit**.
13. Manually test Android onboarding, under-18 gate, return-to-age-selection correction path, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation, restore, delete-all-data, themes, release link, About links/back navigation, and large text.
14. Test Android backup round trips plus malformed/unsupported/oversized/missing-history/non-array-history/all-invalid-history documents and confirm failure occurs without unintended local mutation.
15. Confirm imported legacy Android fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
16. Check Android numeric input/display in at least one dot-decimal and one comma-decimal locale.
17. Manually test desktop adult gate, under-18 path, metric/imperial BMI, metric/imperial waist-to-height, split imperial remaining-inch rejection, theme toggle, About/evidence links, and process restart.
18. Confirm desktop measurements/results/adult choice/theme/navigation state are not retained after closing/reopening the application.
19. Manually launch the JAR and native desktop installer on every platform being published and verify startup, keyboard focus, display scaling, screen-reader naming where available, external-link behavior, install/uninstall behavior, and platform warning/signing expectations.
20. Capture/review Android release screenshots using fictional/example data only.
21. Complete the Android TalkBack/accessibility checklist and desktop accessibility checklist and record evidence.
22. Confirm no secrets/signing material are in Git history.
23. Configure production Android signing only in a protected distribution environment.
24. Configure desktop code signing/notarization outside source control if signed installers are being promoted as production assets.
25. Create the release tag only after all blockers above are closed.

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
python3 -m unittest discover -s scripts/tests -p "test_*.py"
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

The repository audit includes an exhaustive documentation-integrity check against `git ls-files`; a tracked file that is missing from `docs/repository-file-reference.md` fails preflight.

To validate a proposed stable tag against project versions before pushing it:

```bash
python3 scripts/check_release_version.py v0.1.0
```

Native desktop packages are host-specific:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

With a connected Android emulator/device:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, compile the shared Apple targets with:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## Automated workflow gates

Before tagging, the exact release commit should already have passed:

- `CI` — repository/docs audits including exhaustive tracked-file documentation, Python repository-tooling regression tests, shared/Android/desktop formatting, shared tests, desktop tests/JAR packaging, Android JVM tests/lint/build/APK/AAB;
- `Desktop` — desktop formatting/tests, runnable-JAR packaging, and native installer packaging on Linux, Windows, and macOS;
- `Android instrumentation` — connected tests on the configured API 35 emulator plus the eight-file `android-release-screenshots` evidence artifact;
- `Apple shared core` — shared JVM tests plus iOS device/simulator compilation on macOS;
- `CodeQL`;
- `Dependency Review` where applicable;
- `Secret Scan`.

The tagged release workflow does not replace these pull-request/main gates. Results from an older branch head are not release evidence for a newer commit.

## Android release artifacts

Repository builds create:

- unsigned release APK under `androidApp/build/outputs/apk/release/`;
- unsigned release App Bundle under `androidApp/build/outputs/bundle/release/`.

The App Bundle is the preferred input for a protected Google Play signing/distribution pipeline. The APK remains useful for controlled release-candidate inspection/testing.

Neither repository artifact should be represented as a production-store-signed binary. Production signing must happen outside Git source using protected credentials.

## Desktop release artifacts

The desktop module builds a current-operating-system runnable JAR with:

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

It also builds host-specific native installers:

- Linux: `gradle :desktopApp:packageDeb` → `desktopApp/build/compose/binaries/main/deb/*.deb`;
- Windows: `gradle :desktopApp:packageMsi` → `desktopApp/build/compose/binaries/main/msi/*.msi`;
- macOS: `gradle :desktopApp:packageDmg` → `desktopApp/build/compose/binaries/main/dmg/*.dmg`.

The public desktop application/release version and platform-native installer metadata are related but not always represented identically by every packaging tool. The current macOS DMG configuration uses a positive-major native package version while the public HealthMetric release remains `0.1.0`; the release tag/file names remain the source of truth for the public release version.

The cross-platform Desktop workflow verifies both the runnable JAR and native installer on each matching host. The tagged release workflow stages and publishes both forms for each platform after the build jobs succeed.

A successful package build proves reproducible creation, not production trust. Human host-platform smoke testing remains required before promotion. Unsigned/unnotarized installers may trigger platform warnings; signing/notarization credentials must remain outside source control.

## Android release data/privacy checks

Before tagging, verify:

- fresh install history is disabled;
- selecting 50/100/250/500 retention trims history as documented;
- raw weight, height, and waist fields do not appear in persisted history/export;
- individual deletion can be undone without enabling future history saving;
- restored/undone entries maintain newest-first chronology;
- erase-all requires confirmation;
- delete-all returns to onboarding/privacy defaults;
- an accidental under-18 selection can return to age selection without clearing unrelated local history/preferences;
- file backup uses Android's document picker;
- share backup uses an explicit chooser;
- restore asks for confirmation before mutation;
- restore rejects unsupported schema versions and oversized payloads;
- restore rejects missing or non-array top-level `history` before local mutation;
- explicit `history: []` remains a valid intentional empty-history backup;
- a non-empty `history` array is rejected if no valid record survives sanitation;
- a structurally valid non-empty array with valid neighbors can salvage them while skipping malformed records;
- duplicate IDs cannot become duplicate Compose list keys;
- imported history never exceeds the selected supported retention limit;
- extreme finite imported history values cannot overflow chart normalization;
- portable backup JSON omits `historyEnabled`, `adultUseConfirmed`, and `onboardingComplete`;
- legacy backups containing those fields cannot change current device-local consent/safety state;
- app manifest still has no Internet permission and keeps Android backup disabled.

The authoritative schema contract is [`backup-format.md`](backup-format.md).

## Android release screenshot evidence

`ReleaseScreenshotCaptureTest` drives the real Android app and writes the required PNG set to app-scoped external storage. The Android instrumentation workflow pulls those files and publishes `android-release-screenshots`.

The required set is:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

A successful artifact upload is automated evidence that the files were generated. A human must still inspect them for visual defects, clipping, accidental private data, inappropriate sample values, and suitability for permanent README/store publication.

## Desktop release privacy/input checks

Before publishing a desktop artifact, verify:

- adult calculators are unavailable until the adult option is explicitly selected;
- the under-18 path does not reveal adult reference results;
- calculator inputs/results reset after application restart;
- adult-use selection resets after application restart;
- theme/navigation selection resets after application restart;
- no desktop measurement/history file or preferences store is introduced unintentionally;
- external evidence/repository/funding URLs open only after a button press;
- shared calculation/reference rules remain sourced from `shared` rather than copied into desktop UI code;
- split imperial height treats the second component as **remaining inches**, requiring `[0, 12)` rather than silently normalizing values such as 12 or 20 inches into extra feet.

See [`desktop.md`](desktop.md) and ADR 0005.

## Locale/input checks

Android release checks should include at least one dot-decimal and one comma-decimal locale and confirm displayed precision/history/chart summaries remain consistent.

Desktop input accepts ordinary dot or comma decimals through its presentation parser. Verify both forms on at least one release platform. Shared arithmetic remains locale-independent. Scientific notation, signed values, non-finite literals, malformed mixed-separator values, and invalid split remaining-inch components are intentionally rejected by the desktop presentation boundary.

## Documentation release checks

Documentation is part of release integrity.

Before tagging:

- `docs/repository-file-reference.md` must cover every exact tracked file;
- `docs/documentation-map.md` must still identify the canonical owner for each detailed contract;
- local Markdown links must pass;
- README/platform/privacy/backup/testing/release documents must agree about persistence, adult-use, supported targets, and artifact status;
- `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` must describe the exact candidate rather than an earlier head;
- manual gates must remain unchecked/unclaimed until actually completed.

## Tagging

Create an annotated tag only after the release commit is ready:

```bash
git tag -a v0.1.0 -m "HealthMetric v0.1.0"
git push origin v0.1.0
```

Tags matching `v*` trigger `.github/workflows/release.yml`. The workflow then independently rejects tags that are not stable `vMAJOR.MINOR.PATCH`, do not match both configured app versions, or do not point to the current `main` commit.

## Automated tagged release workflow

The tagged workflow separates preflight, build verification, deterministic staging, and publication.

### Preflight job

Before any release artifact is built, preflight:

- checks out complete history so the tag can be compared with `main`;
- runs repository invariants, including exhaustive tracked-file documentation coverage;
- runs Markdown-link checks;
- runs the Python repository-tooling regression suite;
- validates that the tag matches Android and desktop project versions;
- requires the tag commit to equal the current `main` commit.

The workflow defaults to `contents: read`. Only the final publish job receives `contents: write`.

### Android release job

After preflight succeeds, the Android job:

- sets up Python, JDK, Gradle, and Android SDK;
- runs shared tests/formatting plus Android formatting, unit tests, and release lint;
- creates unsigned APK and AAB;
- calls `scripts/stage_release_assets.py`, which requires exactly one non-empty expected APK and AAB and gives them deterministic versioned names;
- uploads the staged Android assets as a workflow artifact.

### Desktop release matrix

On Linux, Windows, and macOS it:

- runs desktop formatting and tests;
- packages the current-OS runnable JAR;
- packages DEB/MSI/DMG respectively;
- calls the same cross-platform staging script, which requires exactly one non-empty JAR and native installer per host;
- uploads each platform asset set as a workflow artifact.

### Publish job

The publish job starts only after Android and all desktop matrix jobs succeed. It downloads the verified artifacts and `scripts/verify_release_assets.py` requires exactly this complete non-empty set before publication:

- Android unsigned APK;
- Android unsigned AAB;
- Linux runnable JAR;
- Linux DEB;
- Windows runnable JAR;
- Windows MSI;
- macOS runnable JAR;
- macOS DMG.

Missing, extra, or empty files fail closed. The verifier writes `SHA256SUMS.txt` covering the eight binary artifacts. The release command uses `--verify-tag`, then creates one GitHub Release with generated notes, all verified binaries, and the checksum manifest.

## Signing and platform trust

### Android

Production Android signing must happen through a protected environment using secrets that are never committed. For Google Play, use Play App Signing or another protected release pipeline for the App Bundle.

### Desktop

The repository can reproducibly build native installers, but build success does not imply production code signing/notarization. If signed/notarized desktop installers are distributed, protect certificates, private keys, passwords, and notarization credentials outside source control and document the reviewed release process.

## Rollback

If a release has a blocker defect:

- mark the release as affected in release notes;
- fix on `main` with regression coverage;
- publish a patch release;
- do not rewrite a published Git tag to hide history.

If Android backup behavior changes, retain compatibility with supported schema versions or explicitly document a migration before shipping.

If evidence/reference interpretation changes, ship it as an explicit versioned profile change rather than silently mutating a published interpretation.

If desktop persistence is introduced, update ADR 0005 or supersede it with a reviewed persistence decision before release.

If tracked-file/documentation ownership changes, reconcile the exhaustive file reference/documentation map in the patch rather than disabling the invariant.

## Release notes content

Include:

- user-visible changes by platform;
- privacy/data changes;
- portable backup/migration changes;
- reference/evidence changes and source review date where applicable;
- accessibility improvements;
- platform/shared-core target changes;
- Android APK/App Bundle packaging changes;
- desktop JAR/native-installer artifact changes;
- release-integrity/checksum changes;
- documentation/governance changes that materially affect contributors/release process;
- fixed defects and regression coverage;
- known limitations;
- exact verification status.
