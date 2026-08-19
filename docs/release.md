# Release Guide

## Release principles

A HealthMetric release is publishable only after reproducible build, test, lint, privacy, accessibility, evidence, documentation, and platform checks pass.

The repository never stores Android signing keys, signing passwords, production credentials, or private certificates.

HealthMetric currently has:

- an Android application;
- a Compose Multiplatform desktop client for JVM environments;
- a Kotlin Multiplatform shared calculation core with Android, JVM/Desktop, iOS device, and iOS simulator targets.

A green build is necessary but not sufficient. Physical-device testing, assistive-technology review, screenshot approval, target-host smoke testing, and protected signing/notarization remain human/external gates where documented.

Current release target: **2.0.12** (`v2.0.12`).

## Versioning

Use Semantic Versioning:

- patch: compatible bug/security/documentation fixes;
- minor: compatible features;
- major: intentionally incompatible behavior/data contracts.

For the `2.0.12` release candidate:

- Android `versionName` is `2.0.12`;
- Android `versionCode` is `20012`;
- desktop project `version` is `2.0.12`;
- desktop native `packageVersion` is `2.0.12` for Linux, Windows, and macOS;
- the stable release tag is `v2.0.12`.

The repository maps a public semantic version to Android `versionCode` as:

`MAJOR * 10000 + MINOR * 100 + PATCH`

The mapping reserves two digits each for `MINOR` and `PATCH`. `scripts/check_release_version.py` rejects release preparation when the semantic components cannot be represented by this mapping or when Android/desktop version metadata disagree.

For every release:

- keep Android `versionCode` monotonic;
- update Android `versionName` to the public release version;
- update the desktop project version and native package version to the public release version;
- require the release tag to use stable `vMAJOR.MINOR.PATCH` form and match Android `versionName`, the derived Android `versionCode`, the desktop project version, and the desktop native package version;
- create the release tag only on the current `main` commit; the tagged workflow fails closed when the tag targets any other commit;
- update release notes and changelog consistently.

The previous `0.1.0` development configuration needed a macOS package-version workaround because that package metadata required a positive major component. The `2.0.12` target no longer needs that override; all desktop package metadata now uses `2.0.12` directly.

## Pre-release checklist

1. Update `CHANGELOG.md`.
2. Update `ROADMAP.md` and `what_changed.md`.
3. Confirm [`documentation-map.md`](documentation-map.md) still identifies the canonical current contracts.
4. Confirm [`repository-file-reference.md`](repository-file-reference.md) contains every exact `git ls-files` path and no tracked file was added without responsibility documentation.
5. Verify Android `versionName`, `versionCode`, desktop `version`, desktop `packageVersion`, and the proposed tag with `scripts/check_release_version.py`.
6. Verify reference source metadata, `reviewedOnIsoDate`, and adult-only copy.
7. Run repository invariants, repository-tooling regression tests, and internal Markdown-link checks.
8. Run shared, Android, and desktop formatting/tests.
9. Run Android release lint, debug assembly, unsigned release APK assembly, and unsigned release App Bundle assembly.
10. Package the desktop runnable JAR and native installer on every desktop operating-system family being published: DEB on Linux, MSI on Windows, DMG on macOS.
11. Run connected Android instrumentation on an emulator/device and inspect the generated screenshot evidence set.
12. Compile the iOS shared-core targets on macOS.
13. Confirm GitHub CI, Desktop, Android instrumentation, Apple shared core, CodeQL, dependency review, and secret scanning are green for the **exact release PR/commit**.
14. Manually test Android onboarding, under-18 gate, return-to-age-selection correction path, BMI, ratio, history disabled/enabled, retention changes, entry deletion/undo, erase-all confirmation, file backup, share backup, restore confirmation, restore, delete-all-data, themes, release link, About links/back navigation, and large text.
15. Test Android backup round trips plus malformed UTF-8, unsupported schema, oversized, missing-history, non-array-history, and all-invalid-history documents and confirm failure occurs without unintended local mutation.
16. Confirm imported legacy Android fields cannot alter history opt-in, adult-use confirmation, or onboarding state.
17. Check Android numeric input/display in at least one dot-decimal and one comma-decimal locale.
18. Manually test desktop adult gate, under-18 path, metric/imperial BMI, metric/imperial waist-to-height, split imperial remaining-inch rejection, theme toggle, About/evidence links, and process restart.
19. Confirm desktop measurements/results/adult choice/theme/navigation state are not retained after closing/reopening the application.
20. Manually launch the JAR and native desktop installer on every platform being published and verify startup, keyboard focus, display scaling, screen-reader naming where available, external-link behavior, install/uninstall behavior, and platform warning/signing expectations.
21. Capture/review Android release screenshots using fictional/example data only.
22. Complete the Android TalkBack/accessibility checklist and desktop accessibility checklist and record evidence.
23. Confirm no secrets/signing material are in Git history.
24. Configure production Android signing only in a protected distribution environment.
25. Configure desktop code signing/notarization outside source control if signed installers are being promoted as production assets.
26. Create the release tag only after all blockers above are closed.

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

Validate the current stable release target before pushing a tag:

```bash
python3 scripts/check_release_version.py v2.0.12
```

That command validates the tag form plus Android `versionName`, Android semantic `versionCode` mapping, desktop project version, and desktop native package version.

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

For `v2.0.12`, deterministic release staging names the Android binaries:

- `healthmetric-v2.0.12-android-unsigned.apk`;
- `healthmetric-v2.0.12-android-unsigned.aab`.

## Desktop release artifacts

The desktop module builds a current-operating-system runnable JAR with:

```bash
gradle :desktopApp:packageUberJarForCurrentOS
```

It also builds host-specific native installers:

- Linux: `gradle :desktopApp:packageDeb` → `desktopApp/build/compose/binaries/main/deb/*.deb`;
- Windows: `gradle :desktopApp:packageMsi` → `desktopApp/build/compose/binaries/main/msi/*.msi`;
- macOS: `gradle :desktopApp:packageDmg` → `desktopApp/build/compose/binaries/main/dmg/*.dmg`.

For `2.0.12`, the public desktop version and native package version are identical on all three hosts. Deterministic staged names are:

- `healthmetric-v2.0.12-desktop-linux.jar`;
- `healthmetric-v2.0.12-desktop-linux.deb`;
- `healthmetric-v2.0.12-desktop-windows.jar`;
- `healthmetric-v2.0.12-desktop-windows.msi`;
- `healthmetric-v2.0.12-desktop-macos.jar`;
- `healthmetric-v2.0.12-desktop-macos.dmg`.

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
- restore rejects malformed UTF-8 before JSON parsing;
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
- `CHANGELOG.md`, `ROADMAP.md`, and `what_changed.md` must identify `2.0.12` as the candidate rather than an older planned release;
- manual gates must remain unchecked/unclaimed until actually completed.

## Tagging

Create an annotated tag only after the release commit is ready:

```bash
git tag -a v2.0.12 -m "HealthMetric v2.0.12"
git push origin v2.0.12
```

Tags matching `v*` trigger `.github/workflows/release.yml`. The workflow then independently rejects tags that are not stable `vMAJOR.MINOR.PATCH`, do not match configured Android/desktop version metadata, violate the Android version-code mapping, or do not point to the current `main` commit.

## Automated tagged release workflow

The tagged workflow separates preflight, build verification, deterministic staging, and publication.

### Preflight job

Before any release artifact is built, preflight:

- checks out complete history so the tag can be compared with `main`;
- runs repository invariants, including exhaustive tracked-file documentation coverage;
- runs Markdown-link checks;
- runs the Python repository-tooling regression suite;
- validates the tag against Android `versionName`, Android `versionCode`, desktop project version, and desktop native package version;
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
- release-integrity/version/checksum changes;
- documentation/governance changes that materially affect contributors/release process;
- fixed defects and regression coverage;
- known limitations;
- exact verification status.
