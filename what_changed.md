# HealthMetric — Work Handoff

Last updated: 2026-08-19

## Current continuation

Repository:

`https://github.com/sanskarIN/healthmetric`

Primary branch:

`main`

Active integration branch:

`phase3/release-readiness-and-desktop-core`

Active integration pull request:

- PR #14 — `feat: add desktop client and strengthen release verification`
- `https://github.com/sanskarIN/healthmetric/pull/14`

PR #14 is the authoritative continuation branch for the current milestone. It began from `main` commit `66a68b1f062863b2524cfcec43159bae771df13b` and now combines the desktop/release-readiness work with the meaningful Android release-hardening work that had been developed separately in PR #13.

PR #13 (`fix: harden release evidence and final audit`) remains open only until PR #14 finishes exact-head verification and merge. Do not merge PR #13 independently after PR #14 unless a comparison proves that it still contains a meaningful unique change. Its Android correctness/evidence work has been deliberately integrated into PR #14 rather than discarded.

This file is the final content checkpoint for the current PR #14 implementation pass. After this commit, change application/repository files only to fix a concrete final-head verification failure.

## Milestone status

### Repository foundation

Implemented:

- MIT-licensed public repository structure;
- Kotlin/Android/Kotlin Multiplatform build configuration;
- central dependency/version catalog;
- formatting/lint integration;
- contributor/security/privacy/support/community documentation;
- issue and pull-request templates;
- ADR process;
- Dependabot;
- CI, Android instrumentation, desktop, Apple shared-core, CodeQL, Dependency Review, Secret Scan, and tagged release workflows;
- repository invariant audit;
- internal Markdown-link audit;
- release verification scripts for Unix-like systems and Windows PowerShell.

### Shared health domain

Implemented in `shared`:

- adult metric BMI calculation;
- adult imperial BMI calculation;
- adult waist-to-height calculation;
- unit conversions;
- strict finite-number validation;
- plausible measurement bounds;
- versioned adult BMI reference profile;
- evidence publisher/title/URL/note metadata;
- explicit source review date metadata;
- educational, non-diagnostic result notices;
- deterministic boundary/property-style tests;
- Android target;
- JVM/Desktop target;
- iOS device target;
- iOS simulator target.

The shared module remains the authoritative calculation/validation layer. Android and desktop presentation code must not silently duplicate or change adult reference thresholds.

## Android client

### Adult-only safety boundary

Implemented:

- first-run adult-use notice;
- explicit `I am 18 or older` path;
- explicit `I am under 18` path;
- under-18 path does not expose adult reference calculator results;
- adult-use confirmation is device-local state;
- imported backup data cannot set adult-use confirmation or onboarding completion;
- neutral wording avoids diagnosis claims, appearance rankings, or personal body targets.

### Privacy-first history

Android history is optional and disabled by default.

When enabled, persisted history stores only:

- local identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not stored in calculation history.

Retention options:

- 50 entries;
- 100 entries;
- 250 entries;
- 500 entries.

Default retention:

`100`

Hard maximum:

`500`

Lowering retention immediately removes older entries beyond the selected cap.

### Canonical history chronology

A correctness regression from the earlier branch was integrated and fixed in PR #14.

Canonical behavior now is:

1. sanitize accepted entries;
2. deduplicate IDs;
3. sort by `timestampEpochMillis` descending;
4. apply the selected/application retention limit.

The same newest-first rule is used for:

- newly recorded calculations;
- imported backup history;
- restoration of an individually deleted item through Undo.

This prevents an older item from moving to the top when Undo is used and prevents arbitrary JSON array order from determining which records survive retention.

Regression coverage exists in `HealthMetricDataStoreTest`.

### History identifiers

New Android history entries use:

`UUID.randomUUID().toString()`

This replaces the avoidable timestamp-plus-small-random-suffix collision path.

Schema-v1 imported IDs remain backward compatible. They do not have to be UUIDs, but they are:

- trimmed;
- required to be non-blank;
- capped to 96 characters;
- deduplicated after sanitation.

### Individual deletion and Undo

Implemented:

- per-entry delete action;
- accessible delete semantics;
- persisted deletion before feedback;
- snackbar Undo;
- restored entry is sanitized and placed according to canonical timestamp order;
- Undo does not enable future history saving.

Erase-all history remains a separate confirmed destructive action.

### Android backup/export

Settings provides:

- `Save JSON backup to a file`;
- `Share JSON backup`;
- `Restore from JSON backup`.

File save uses Android Storage Access Framework `CreateDocument`.

Share export uses an explicit chooser.

Restore uses a selected document and asks for confirmation before persistence mutation.

### Backup schema and bounds

Current portable Android schema version:

`1`

Portable top-level fields:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- `history`.

Deliberately non-portable state:

- history-saving consent;
- adult-use confirmation;
- onboarding completion.

Backup payload maximum:

`1,048,576 bytes` (1 MiB UTF-8)

Protections include:

- bounded streaming read;
- bounded write;
- independent restore-boundary UTF-8 size check;
- top-level schema validation;
- supported retention normalization;
- theme normalization;
- malformed-record recovery;
- non-blank/length-bounded IDs;
- non-negative timestamps;
- finite values;
- known calculator types only;
- bounded summaries;
- duplicate-ID removal;
- newest-first chronology normalization;
- retention applied after chronology normalization;
- device-local consent/adult-gate state preservation.

Authoritative contract:

`docs/backup-format.md`

Architecture decision:

`docs/adr/0004-bounded-user-controlled-local-data.md`

### About navigation fix

The Android About destination previously hid bottom navigation without providing a reliable in-app return path.

PR #14 now includes:

- explicit top-app-bar Back action while About is open;
- previous destination tracking;
- Android system Back handling;
- stable About open/back test tags;
- stable bottom-navigation test tags;
- `AboutNavigationUiTest` covering return to BMI and Settings origins.

### Android release screenshot evidence

`ReleaseScreenshotCaptureTest` now drives the real Android application and captures eight required PNGs using fictional/example measurements:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

The test resets app state before capture.

`.github/workflows/android-instrumentation.yml`:

- runs the connected API 35 Pixel 7 emulator suite;
- pulls the app-scoped `release-screenshots` directory with `adb pull`;
- uploads `android-release-screenshots`;
- treats missing PNG evidence as an artifact-upload failure;
- separately uploads instrumentation reports.

Automated screenshot creation is not represented as final marketing approval. Human visual/privacy review is still required before committing/publishing permanent screenshots.

### Android release artifacts

Configured/verified build tasks include:

- debug APK: `:androidApp:assembleDebug`;
- unsigned release APK: `:androidApp:assembleRelease`;
- unsigned release App Bundle: `:androidApp:bundleRelease`.

Production signing is intentionally outside source control.

## Desktop client

### Product scope

PR #14 adds a Compose Multiplatform desktop application in `desktopApp`.

Implemented desktop journeys:

- explicit adult-use gate;
- separate under-18 unavailable path;
- metric adult BMI;
- imperial adult BMI;
- metric adult waist-to-height;
- imperial adult waist-to-height;
- dot/comma decimal input;
- shared-domain validation/reference behavior;
- neutral educational results;
- light/dark session theme;
- About/evidence/project/support/funding information;
- explicit external-link actions.

### Desktop privacy model

Desktop intentionally has no HealthMetric measurement/session persistence layer.

It does not persist:

- entered weight/height/waist;
- BMI/ratio results;
- adult-use selection;
- theme selection;
- calculator/navigation state.

Those values remain in process memory and are discarded when the desktop application closes.

Desktop does not currently import/export Android backup files.

Architecture decision:

`docs/adr/0005-ephemeral-desktop-client.md`

Desktop guide:

`docs/desktop.md`

### Desktop code structure

Important files:

- `desktopApp/build.gradle.kts`;
- `desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/Main.kt`;
- `DesktopCalculations.kt`;
- `DesktopNumbers.kt`;
- `DesktopCalculationsTest.kt`;
- `DesktopNumbersTest.kt`.

`DesktopCalculations.kt` adapts the shared domain for desktop presentation.

`DesktopNumbers.kt` provides finite dot/comma decimal parsing and whole-number parsing for imperial feet.

### Desktop tests

`DesktopNumbersTest` covers:

- dot decimals;
- comma decimals;
- whitespace;
- malformed decimals;
- non-finite values;
- whole-number feet parsing.

`DesktopCalculationsTest` covers:

- metric BMI shared-core integration;
- imperial BMI shared-core conversion;
- waist-to-height presentation;
- field-specific invalid input;
- preservation of shared validation bounds.

### Desktop runnable JAR

Build task:

`gradle :desktopApp:packageUberJarForCurrentOS`

Output:

`desktopApp/build/compose/jars/*.jar`

### Native desktop installers

The module configures:

- Linux DEB;
- Windows MSI;
- macOS DMG.

Host-specific tasks:

- Linux: `:desktopApp:packageDeb`;
- Windows: `:desktopApp:packageMsi`;
- macOS: `:desktopApp:packageDmg`.

Expected output directories:

- `desktopApp/build/compose/binaries/main/deb/`;
- `desktopApp/build/compose/binaries/main/msi/`;
- `desktopApp/build/compose/binaries/main/dmg/`.

`.github/workflows/desktop.yml` runs on Linux, Windows, and macOS and now verifies:

- desktop ktlint;
- desktop tests;
- current-OS runnable JAR;
- matching native installer;
- artifact upload containing both runnable JAR and native installer.

Build success is not represented as production code-signing/notarization approval. Human host-platform install/uninstall/accessibility/smoke testing remains a release gate.

## Apple shared core

The shared module includes:

- `iosArm64`;
- `iosSimulatorArm64`.

`.github/workflows/apple-shared.yml` compiles both on macOS and also verifies shared JVM tests.

No iOS user interface is claimed.

## Repository automation

### Main CI

`.github/workflows/ci.yml` verifies:

- repository invariants;
- internal Markdown links;
- JDK 17 / Gradle 8.13 setup;
- Android SDK setup;
- shared/Android/desktop ktlint;
- shared JVM tests;
- desktop JVM tests;
- desktop runnable-JAR packaging;
- Android JVM tests;
- Android release lint;
- debug APK;
- unsigned release APK;
- unsigned release AAB;
- relevant artifact uploads.

Native desktop installers are validated by the dedicated host matrix rather than by the Ubuntu-only main CI job.

### Desktop workflow

`.github/workflows/desktop.yml` matrix:

- Ubuntu → JAR + DEB;
- Windows → JAR + MSI;
- macOS → JAR + DMG.

### Android instrumentation

`.github/workflows/android-instrumentation.yml` verifies connected Android tests and publishes real-app release screenshot evidence.

### Apple shared core

`.github/workflows/apple-shared.yml` verifies shared JVM tests plus iOS simulator/device compilation on macOS.

### Security automation

Configured:

- CodeQL;
- Dependency Review;
- full-history Secret Scan;
- Dependabot.

### Tagged release workflow

`.github/workflows/release.yml` separates verification/build from publication.

Android job creates/stages:

- `healthmetric-<tag>-android-unsigned.apk`;
- `healthmetric-<tag>-android-unsigned.aab`.

Desktop host matrix creates/stages:

- `healthmetric-<tag>-desktop-linux.jar`;
- `healthmetric-<tag>-desktop-linux.deb`;
- `healthmetric-<tag>-desktop-windows.jar`;
- `healthmetric-<tag>-desktop-windows.msi`;
- `healthmetric-<tag>-desktop-macos.jar`;
- `healthmetric-<tag>-desktop-macos.dmg`.

The publish job starts only after Android and all desktop build jobs succeed, downloads the versioned assets, checks the complete expected eight-file set is non-empty, and then creates the GitHub Release.

## Repository invariant audit

`scripts/check_repository.py` now combines Android, shared, desktop, release, and documentation invariants.

It checks, among other requirements:

- required project/community/documentation paths exist;
- Android manifest requests no Internet permission;
- Android backup stays disabled;
- Android cleartext traffic stays disabled;
- the desktop module is included;
- desktop depends on the shared module;
- desktop native distribution formats remain configured;
- required adult/privacy desktop copy remains present;
- README retains project credit/funding/contact/license/desktop documentation metadata;
- PRIVACY keeps Android and desktop privacy boundaries;
- Android AAB tasks/artifacts remain in CI/release/local verification;
- Android screenshot workflow/artifact configuration remains present;
- all eight screenshot names remain in the capture test;
- desktop workflow contains tests, JAR packaging, and DEB/MSI/DMG packaging;
- release workflow retains all native desktop release assets;
- accidental `docs/.noop-probe` is forbidden.

## Internal documentation link audit

`scripts/check_markdown_links.py` validates local relative Markdown targets without requiring network access.

## Local verification scripts

`scripts/verify.sh` and `scripts/verify.ps1` run the portable non-device suite including:

- shared/Android/desktop formatting;
- shared JVM tests;
- desktop JVM tests;
- desktop runnable JAR packaging;
- Android JVM tests;
- Android release lint;
- debug APK;
- unsigned release APK;
- unsigned release AAB.

Native desktop installers remain host-specific commands and are validated in the multi-OS Desktop workflow.

## Documentation reconciled in this continuation

Updated or added documentation includes:

- `README.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `CONTRIBUTING.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `.github/PULL_REQUEST_TEMPLATE.md`;
- `.github/RELEASE_TEMPLATE.md`;
- `docs/architecture.md`;
- `docs/desktop.md`;
- `docs/setup.md`;
- `docs/development.md`;
- `docs/testing.md`;
- `docs/release.md`;
- `docs/troubleshooting.md`;
- `docs/accessibility.md`;
- `docs/performance.md`;
- `docs/github-governance.md`;
- `docs/backup-format.md`;
- `docs/assets/screenshots/README.md`;
- `docs/adr/0004-bounded-user-controlled-local-data.md`;
- `docs/adr/0005-ephemeral-desktop-client.md`;
- this `what_changed.md`.

Documentation now distinguishes:

- persistent, opt-in Android history/backup behavior;
- ephemeral desktop session behavior;
- automated build/evidence success;
- manual release acceptance/signing/notarization tasks that automation cannot truthfully claim complete.

## Important integration decision: PR #13 into PR #14

PR #13 and PR #14 were created from different continuation paths.

PR #14 was newer and contained the desktop client/release-readiness work but initially lacked several correctness/release-evidence improvements from PR #13.

Rather than merge the stale branches independently and risk conflicts/regressions, the meaningful PR #13 work was ported into PR #14 as separate focused commits:

- canonical Android history chronology;
- UUID Android history identifiers;
- Android About return navigation;
- navigation test tags;
- About navigation instrumentation tests;
- real-app screenshot capture instrumentation;
- Android screenshot artifact publishing;
- chronology/restore regression coverage;
- removal/forbidding of `docs/.noop-probe`;
- Android backup/ADR/screenshot documentation;
- Android AAB/release-evidence invariant coverage.

Then those changes were reconciled with PR #14's:

- desktop application;
- desktop privacy model;
- desktop tests;
- desktop multi-OS CI;
- desktop documentation;
- multiplatform release workflow.

This avoids losing either branch's meaningful work.

## Commit strategy

This continuation deliberately uses many meaningful Conventional Commits instead of one giant commit.

Recent focused commits in this integration include:

- `fix: preserve canonical newest-first history ordering`;
- `fix: use collision-resistant UUID history identifiers`;
- `fix: restore deterministic About back navigation`;
- `test: add stable navigation semantics tags`;
- `feat: add accessible About back label`;
- `test: cover About return navigation`;
- `test: capture reproducible Android release evidence`;
- `test: verify chronology across restore and undo`;
- `chore: remove accidental repository probe file`;
- `ci: publish real-app Android screenshot evidence`;
- `ci: unify Android and desktop repository invariants`;
- `docs: reconcile desktop and Android release changelog`;
- `docs: reconcile multiplatform release roadmap`;
- `ci: verify native desktop installers on every host`;
- `release: publish native desktop installers with release assets`;
- `ci: enforce native desktop release packaging`;
- `docs: document native desktop release artifacts`;
- `docs: document verified native desktop packaging`;
- `docs: document automated Android screenshot evidence`;
- `docs: align backup contract with canonical history and desktop scope`;
- `docs: codify Android chronology and UUID data contract`;
- `docs: expand multiplatform release verification template`;
- `docs: align test matrix with screenshot and native package gates`;
- `docs: publish complete multiplatform project overview`;
- this final handoff update.

Git history is authoritative for exact commit hashes.

Requested commit email for project work:

`sanskarin@outlook.in`

## What remains before public `v0.1.0`

The implementation/repository work in this milestone is intended to be complete subject to exact-head automation. The following are intentionally not falsely marked complete because they require actual release hardware, human review, or protected credentials:

### Exact-head automated verification

Before PR #14 merge, confirm the final `what_changed.md` head is green across:

- CI;
- Android instrumentation;
- Desktop;
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

Also confirm:

- `android-release-screenshots` exists and contains the required eight PNGs;
- desktop verification artifacts include the runnable JAR plus DEB/MSI/DMG on their matching runners.

Any real failure must receive the smallest root-cause fix plus regression/invariant coverage where appropriate.

### Human/device release acceptance

Still required before public release:

- physical Android release-candidate smoke testing;
- TalkBack review;
- maximum-font/display scaling review;
- keyboard/DPAD review where applicable;
- human visual/privacy review of the exact CI-generated Android screenshot set;
- desktop JAR/native installer smoke testing on Linux, Windows, and macOS;
- desktop keyboard/focus/display-scaling/screen-reader review on published platforms;
- desktop install/uninstall review;
- platform warning/signing/notarization behavior review.

### Protected signing/trust setup

Still external to source control:

- Android production signing / Play App Signing pipeline;
- Android signing secrets/passwords;
- desktop signing certificates/private keys if signed installers are distributed;
- macOS notarization credentials if notarized DMGs are distributed.

No secret/signing material should be committed to the repository.

### Release tag

Do not create `v0.1.0` until:

1. exact release-candidate automation is green;
2. Android physical-device/accessibility/screenshot review is complete;
3. desktop target-host smoke/accessibility review is complete for artifacts being published;
4. protected signing/trust decisions are configured/documented;
5. version/changelog/roadmap/handoff/release notes match the exact release commit.

## Next exact actions for this continuation

After this handoff commit:

1. fetch PR #14 metadata and exact head SHA;
2. inspect only workflow runs associated with that exact head;
3. wait for/inspect CI, Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review, and Secret Scan;
4. if a workflow fails, inspect its jobs/logs and fix the root cause on PR #14;
5. verify Android screenshot artifact contents;
6. verify desktop artifact sets from Linux/Windows/macOS;
7. merge PR #14 into `main` using a normal merge commit to preserve the meaningful granular history;
8. inspect post-merge `main` automation;
9. compare PR #13 against merged `main` and close PR #13 as superseded only if no meaningful unique change remains;
10. update this handoff on `main` with the actual merged/final status if a post-merge documentation-only change is needed;
11. do not create `v0.1.0` until the manual/signing release blockers above are actually completed.
