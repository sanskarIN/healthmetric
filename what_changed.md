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

The top-level `history` field is required to be a JSON array. Missing or wrong-type `history` is rejected before DataStore mutation; an empty array is valid.

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
- required top-level history-array validation before mutation;
- supported retention normalization;
- theme normalization;
- malformed-record recovery inside a structurally valid history array;
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
- repository/release tooling regression tests;
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

`.github/workflows/release.yml` separates preflight verification/build from publication.

Preflight requires:

- stable `vMAJOR.MINOR.PATCH` tag form;
- tag version matching Android and desktop public versions;
- tag commit equal to the current `main` commit;
- repository/docs audits;
- repository/release tooling tests.

Release permissions are read-only by default; only the final publication job has `contents: write`.

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

Staging requires exactly one non-empty expected build output per artifact type. The publish job starts only after Android and all desktop build jobs succeed, requires exactly the expected eight non-empty binaries with no extras, writes `SHA256SUMS.txt`, verifies the tag, and then creates the GitHub Release.

## Repository invariant audit

`scripts/check_repository.py` now combines Android, shared, desktop, release, and documentation invariants.

It checks, among other requirements:

- required project/community/documentation paths exist;
- Android manifest requests no Internet permission;
- Android backup stays disabled;
- Android cleartext traffic stays disabled;
- Android restore requires a top-level `history` JSON array before mutation;
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
- release tag/version/main/checksum/permission invariants remain present;
- accidental `docs/.noop-probe` is forbidden.

## Internal documentation link audit

`scripts/check_markdown_links.py` validates local relative Markdown targets without requiring network access.

## Local verification scripts

`scripts/verify.sh` and `scripts/verify.ps1` run the portable non-device suite including:

- repository/docs audits;
- repository/release tooling regression tests;
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
3. inspect CI, Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review, and Secret Scan;
4. if a workflow fails, inspect its jobs/logs and fix the root cause on PR #14;
5. verify Android screenshot artifact contents;
6. verify desktop artifact sets from Linux/Windows/macOS;
7. merge PR #14 into `main` using a normal merge commit to preserve the meaningful granular history only after all required exact-head automation is green;
8. inspect post-merge `main` automation;
9. compare PR #13 against merged `main` and close PR #13 as superseded only if no meaningful unique change remains;
10. update this handoff on `main` with the actual merged/final status if a post-merge documentation-only change is needed;
11. do not create `v0.1.0` until the manual/signing release blockers above are actually completed.

## Continuation pass — correctness, state safety, and release integrity

This section records the additional implementation pass performed after the earlier handoff above. It supersedes the older implication that only workflow-failure fixes remained, while preserving the earlier history unchanged.

### Shared-domain corrections completed

Imperial validation was audited end to end rather than only at the presentation layer.

Completed fixes:

- imperial weight range errors now report pounds rather than kilogram limits;
- imperial waist range errors now report inches rather than centimeter limits;
- `BmiCalculator.calculateImperial` no longer validates in imperial units and then revalidates converted values through the metric path;
- documented imperial BMI boundary values therefore remain valid after conversion instead of being rejected by slightly different metric conversion boundaries;
- `WaistToHeightCalculator.calculateImperial` now preserves an imperial validation contract rather than converting and reusing the metric validator;
- metric and imperial error messages remain unit-specific and adult/educational in wording.

Regression coverage now locks:

- imperial pound error text;
- imperial inch error text;
- lower and upper documented imperial BMI weight boundaries;
- lower and upper documented imperial waist/height boundaries;
- desktop presentation of those unit-specific errors.

### Desktop input hardening completed

`DesktopNumbers` now accepts ordinary measurement-number syntax only.

Accepted examples include:

- `72`;
- `72.5`;
- `72,5`;
- `.5`;
- `72.` after trimming surrounding whitespace.

Rejected examples include:

- scientific notation such as `1e2`;
- explicit signed values such as `+72.5` and `-72.5`;
- `NaN`;
- `Infinity`;
- malformed mixed/double separators;
- non-whole imperial feet.

This keeps desktop measurement entry predictable while domain validation remains authoritative for supported adult ranges.

### Android chart arithmetic hardening completed

Schema-v1 history intentionally accepts any finite stored calculation value that passes history sanitation. Extreme but finite imported values could previously make `max - min` overflow to infinity during chart normalization.

Added:

- `ChartScale.kt`;
- scale-first normalization based on the maximum absolute finite magnitude;
- flat-series centering;
- explicit non-finite rejection at the chart utility boundary;
- unit tests including `-Double.MAX_VALUE`, zero, and `Double.MAX_VALUE`;
- `HistoryScreen` integration using `ChartScale.normalize` before Canvas coordinates are computed.

This fixes rendering arithmetic without silently changing the portable backup schema contract.

### Android adult-gate correction path completed

An accidental `I am under 18` selection previously persisted `onboardingComplete=true` and `adultUseConfirmed=false`, leaving no in-app path back to age selection.

The correction path now includes:

- `HealthMetricDataStore.resetAdultUseChoice()`;
- ViewModel exposure;
- `Return to age selection` action on the blocked adult-reference screen;
- stable semantics/test tags for adult, under-18, and correction controls;
- Compose instrumentation coverage for the optional correction action;
- DataStore regression coverage proving that resetting the age choice preserves unrelated history opt-in, retention, theme, and saved history.

The adult-only safety boundary is not weakened: adult calculators remain unavailable until the adult choice is explicitly selected again.

### Android saved-state hardening completed

Direct `Enum.valueOf` calls on `rememberSaveable` strings could crash composition after a future enum rename/removal while stale saved state remained.

Added:

- `savedEnumValueOrDefault` helper;
- JVM regression tests for known, stale, and empty saved values;
- safe restoration of main navigation state;
- safe restoration of previous About-origin state;
- safe restoration of the History calculator filter.

Unknown/stale saved names now fall back to a safe default instead of crashing.

### Android backup top-level structure hardening completed

A late audit found that schema-v1 restore used `optJSONArray("history")` and passed a missing/non-array result into the tolerant history decoder. That could interpret a structurally damaged backup as an intentional empty history and, after user confirmation, replace existing history/theme/retention state while reporting restore success.

The restore path now:

- requires `history` to exist as a JSON array after schema validation;
- rejects a missing or non-array `history` before the DataStore edit transaction;
- keeps an empty array valid;
- continues to salvage malformed individual records only after the top-level container is structurally valid.

Regression instrumentation seeds existing history/theme state, attempts both missing-history and object-instead-of-array backups, confirms the restore fails, and verifies the original local data remains unchanged.

`docs/backup-format.md` now defines this fail-closed behavior as part of schema version 1. `scripts/check_repository.py` also locks the required top-level-array guard so later refactors cannot silently remove it.

### Release tag/version preflight completed

Added `scripts/check_release_version.py` with tests.

The tagged workflow now requires:

- stable `vMAJOR.MINOR.PATCH` syntax;
- tag version equal to Android `versionName`;
- tag version equal to the desktop project version;
- Android and desktop public versions to agree;
- tag commit to equal the current `main` commit.

The release checkout uses complete history so this comparison is real rather than inferred from a shallow checkout.

### Release permission hardening completed

`.github/workflows/release.yml` now defaults to:

`contents: read`

Only the final publication job receives:

`contents: write`

Build and verification jobs no longer inherit repository write permission they do not need.

### Deterministic cross-platform release staging completed

Added `scripts/stage_release_assets.py` and regression tests.

The tool is used by Android, Linux, Windows, and macOS release jobs and requires exactly one non-empty expected build output for each artifact type.

It stages deterministic versioned names for:

- Android unsigned APK;
- Android unsigned AAB;
- Linux JAR and DEB;
- Windows JAR and MSI;
- macOS JAR and DMG.

Zero matches, duplicate matches, empty files, unsupported platform values, and invalid release tags fail closed.

This replaces duplicated shell-specific artifact discovery in the release workflow.

### Exact release asset verification and checksums completed

Added `scripts/verify_release_assets.py` and tests.

Before publication it requires exactly the eight expected binary assets and rejects:

- missing assets;
- unexpected extra files;
- empty assets.

After exact-set verification it generates:

`SHA256SUMS.txt`

The release command also uses `gh release create --verify-tag`.

The checksum manifest is published alongside the eight binaries.

### Repository-tooling regression tests integrated everywhere

Python tests under `scripts/tests/` now cover:

- tag/version validation;
- deterministic staging;
- all supported desktop staging targets;
- duplicate/empty output rejection;
- exact final asset-set verification;
- missing/extra/empty final asset rejection;
- deterministic SHA-256 manifest output.

They run in:

- main CI;
- tagged release preflight;
- `scripts/verify.sh`;
- `scripts/verify.ps1`.

### Repository invariant audit expanded

`scripts/check_repository.py` now requires and/or machine-checks the newly established guarantees, including:

- release tag validator;
- release staging/verifier scripts and tests;
- Python release-tooling tests in CI and both local verification scripts;
- read-only default release permissions plus final publish write scope;
- complete-history release preflight;
- version/main checks;
- deterministic staging;
- checksum verification;
- `--verify-tag` publication;
- Android chart finite-safe normalization;
- Android adult-use correction path;
- saved-enum fallback helper/tests;
- required top-level Android backup history-array validation;
- adult-gate UI coverage;
- existing AAB, screenshot, desktop package, privacy, and no-probe invariants.

A stale invariant that still expected direct AAB path handling inside the release YAML was corrected to follow the new staging-script architecture.

### Documentation reconciled in this pass

Updated:

- `CHANGELOG.md`;
- `ROADMAP.md`;
- `.github/RELEASE_TEMPLATE.md`;
- `docs/release.md`;
- `docs/testing.md`;
- `docs/backup-format.md`;
- this `what_changed.md`.

The documentation now explicitly covers:

- unit-specific imperial validation fixes;
- Android chart overflow defense;
- recoverable adult-use selection;
- stale saved-state fallback;
- fail-closed top-level backup structure validation;
- repository-tooling unit tests;
- stable release tag/version/current-main gates;
- least-privilege release permissions;
- deterministic artifact staging;
- exact asset-set verification;
- SHA-256 release checksums;
- remaining manual device/accessibility/signing limitations.

### Additional focused commits from this pass

The pass deliberately continued the granular-commit strategy. Git history contains focused commits for each implementation/test/documentation unit, including commit messages such as:

- `fix(desktop): restrict measurement parser to decimal input`;
- `test(desktop): cover strict measurement number syntax`;
- `fix(shared): report imperial weight validation in pounds`;
- `test(shared): lock unit-specific weight validation messages`;
- `test(desktop): verify imperial range errors use pounds`;
- `fix(shared): avoid metric revalidation of imperial BMI`;
- `test(shared): cover imperial BMI weight boundaries`;
- `fix(shared): add unit-specific imperial waist validation`;
- `fix(shared): avoid metric revalidation of imperial waist ratio`;
- `test(shared): verify imperial waist errors use inches`;
- `test(shared): cover imperial waist ratio boundaries`;
- `test(desktop): verify imperial waist errors use inches`;
- `release: add cross-platform tag version validator`;
- `test(release): cover tag version validation rules`;
- `test(release): derive validator cases from project version`;
- `security(release): scope write permission to publication`;
- `release: gate tags on version and current main`;
- `ci: run release tooling regression tests`;
- `release: add deterministic cross-platform asset staging`;
- `test(release): cover deterministic asset staging`;
- `release: use tested cross-platform asset staging`;
- `release: add exact asset verification and checksums`;
- `test(release): cover exact asset verification and checksums`;
- `release: verify exact asset set and publish checksums`;
- `fix(android): add overflow-safe history chart scaling`;
- `test(android): cover finite-safe chart normalization`;
- `fix(android): render history charts with safe normalized points`;
- `feat(android): allow resetting the adult-use choice`;
- `feat(android): expose adult-use choice reset in view model`;
- `feat(android): label adult gate reset action`;
- `feat(android): add optional age-selection return control`;
- `feat(android): wire age-selection reset through app state`;
- `test(android): verify adult-choice reset preserves local data`;
- `ci: extend repository audit for new release and safety invariants`;
- `fix(android): add safe saved enum restoration helper`;
- `test(android): cover stale saved enum fallback`;
- `fix(android): tolerate stale saved history filter state`;
- `fix(android): tolerate stale saved navigation state`;
- `test(android): add stable age-gate test tags`;
- `test(android): tag age-gate correction controls`;
- `test(android): verify adult gate correction control behavior`;
- `docs: document hardened release integrity workflow`;
- `docs: record calculation, Android, and release hardening fixes`;
- `docs: add release integrity checks to release template`;
- `docs: reconcile roadmap with completed hardening work`;
- `test: run release tooling tests in Unix verification`;
- `test: run release tooling tests in PowerShell verification`;
- `ci: lock saved-state and local verification invariants`;
- `docs: expand regression and release-tooling test matrix`;
- `fix(android): reject malformed top-level backup history`;
- `test(android): protect local data from malformed backup history`;
- `docs: require valid top-level history array before restore`;
- `docs: record fail-closed backup structure validation`;
- `ci: enforce fail-closed backup history structure`;
- `docs: add backup structure regression coverage`.

### Exact-head status after this handoff update

This `what_changed.md` update intentionally becomes the new PR #14 head and therefore restarts/cancels older pull-request workflow attempts through the configured concurrency groups.

Do not use results from an earlier commit as release evidence.

The next verifier must:

1. fetch PR #14 again and record its exact head SHA after this commit;
2. inspect workflow runs only for that SHA;
3. require CI, Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review, and Secret Scan to complete successfully;
4. inspect any failed job logs and make only a concrete root-cause fix with regression/invariant coverage where appropriate;
5. verify the Android instrumentation artifact contains all eight required screenshot PNGs;
6. verify Desktop workflow artifacts contain JAR + DEB on Linux, JAR + MSI on Windows, and JAR + DMG on macOS;
7. only after exact-head automation is green, merge PR #14 into `main` with normal history-preserving merge behavior;
8. inspect post-merge `main` automation;
9. compare PR #13 against merged `main` and close PR #13 as superseded only if no meaningful unique change remains;
10. keep `v0.1.0` untagged until physical Android, TalkBack/visual review, target-host desktop smoke/accessibility review, and protected signing/trust requirements are actually satisfied.

## Deep documentation completion pass

This section records the repository-wide documentation pass performed after the correctness/state/release-integrity work above. The goal was not to inflate documentation volume mechanically; it was to make every tracked file discoverable, assign canonical documentation ownership, and remove stale operational statements from the current branch.

### Exhaustive tracked-file reference completed

Added:

`docs/repository-file-reference.md`

The reference documents every tracked repository file by its exact repository-relative path and explains its responsibility/maintenance boundary. Coverage includes:

- root metadata/configuration/community files;
- every GitHub issue/PR/release template;
- every GitHub Actions workflow;
- Android build/manifest/production Kotlin files;
- every Android resource file;
- every Android JVM and instrumentation test;
- all shared Kotlin Multiplatform production/test files;
- all desktop production/test files;
- Gradle version catalog;
- every Python/shell/PowerShell repository/release script and Python tooling test;
- every project documentation file;
- all five ADRs;
- logo and screenshot-policy assets.

This is the repository's file-by-file answer to the requirement that documentation not skip files.

### Documentation ownership map completed

Added:

`docs/documentation-map.md`

It defines:

- documentation principles;
- entry points by audience (users, contributors, maintainers/release owners);
- the canonical document for each topic;
- a change-type → required-document-review/update matrix;
- ADR creation/supersession policy;
- rules against overstating CI, screenshots, signing, iOS UI, persistence, or medical meaning;
- the maintenance procedure for new/deleted/renamed tracked files.

This prevents future contributors from guessing whether a behavior belongs in README, privacy, backup, architecture, testing, release, evidence, or another specialist document.

### Tracked-file documentation became a machine-checked invariant

`scripts/check_repository.py` now runs `git ls-files -z`, reads the exhaustive repository file reference, and requires every tracked path to occur there exactly in backticks.

Consequences:

- a newly tracked source file cannot remain undocumented while CI is green;
- workflows, scripts, tests, assets, configuration and documentation files receive the same treatment as production Kotlin;
- a deletion/rename must reconcile the old file-reference entry;
- contributors cannot satisfy the intent merely by documenting major modules while silently adding infrastructure files.

The repository invariant also includes the late Android restore guard for non-empty backup history arrays that contain no valid entries.

A wording mismatch in that invariant was found during review (`records` versus the implementation's exact `entries` wording) and corrected immediately so CI would check the real implementation contract rather than fail on a stale string.

### README discoverability completed

README now:

- links the documentation ownership map;
- links the exhaustive tracked-file reference;
- explains that repository CI compares `git ls-files` with the reference;
- documents the stricter Android restore distinction between intentional empty history and non-empty all-invalid history;
- documents strict desktop split-height remaining inches in `[0, 12)`;
- tells contributors to update the file reference for tracked-file structure changes.

### Contributor and PR governance completed

`CONTRIBUTING.md` now includes a dedicated documentation-maintenance contract.

`.github/PULL_REQUEST_TEMPLATE.md` now requires reviewers/contributors to confirm:

- documentation-map review;
- exhaustive file-reference reconciliation;
- exact `git ls-files` coverage;
- canonical architecture/privacy/evidence/testing/release updates where applicable;
- ADR review for durable boundary changes;
- current backup structure/all-invalid behavior;
- desktop split-height input semantics.

`docs/github-governance.md` now treats exhaustive documentation as a repository-governance invariant and explicitly explains why meaningful granular documentation commits are valid while empty/churn commits are not.

### Development/setup/troubleshooting documentation completed

`docs/development.md` now documents exact source/test ownership across shared, Android, and desktop, including Android `ui/state`, platform test placement, documentation ownership, current backup transaction semantics, and split imperial component validation.

`docs/setup.md` fixed a real prerequisite omission: Python 3 is now explicitly required for repository/documentation/release tooling. It documents that the current Python tooling uses the standard library and gives the exact repository/tooling commands.

`docs/troubleshooting.md` now covers:

- missing Python;
- undocumented tracked-file invariant failures;
- why `git ls-files` requires a real Git checkout;
- release-tooling Python test failures;
- desktop remaining-inch validation behavior;
- current three-host native desktop packaging rather than older JAR-only wording;
- missing/non-array/all-invalid Android backup restore failures;
- no-mutation expectations on failed restores;
- chart/saved-state regressions;
- tagged release preflight and exact artifact-set failures;
- exact-head-versus-superseded CI interpretation.

### Architecture, privacy, security, testing, accessibility, design, and performance completed

`docs/architecture.md` now describes:

- explicit dependency direction;
- Android/desktop input and data flows;
- Android saved-state and chart safety boundaries;
- backup structure/all-invalid transaction behavior;
- desktop split-height presentation boundary versus shared adult range validation;
- repository/documentation architecture;
- tagged release trust boundaries.

`PRIVACY.md` now documents the exact schema-v1 restore distinction:

- `history: []` is intentional empty history;
- missing/wrong-type `history` is rejected;
- a non-empty all-invalid history array is rejected before mutation;
- valid neighbors can survive malformed records;
- consent/adult-gate state remains device-local.

`SECURITY.md` now includes the same untrusted-backup model plus chart/saved-state defenses, desktop parser/split-height boundaries, exact release tag/asset validation, least-privilege publication, exhaustive tracked-file documentation, and exact-head security evidence rules.

`docs/testing.md` now includes:

- repository/documentation integrity as its own automated layer;
- all-invalid backup regression behavior;
- desktop remaining-inch boundaries;
- an expanded regression placement policy and validation edge-case matrix.

`docs/accessibility.md` now covers age-gate correction, About back navigation, finite-safe chart alternatives, split-height error discoverability, and exact remaining manual accessibility evidence.

`docs/design-system.md` now documents current interaction/recovery states, test-tag/accessibility separation, desktop split-height form semantics, About return behavior, and finite-safe chart design rules.

`docs/performance.md` now covers bounded restore validation, finite-safe chart arithmetic, desktop input cost, repository/release tooling cost, and performance-specific release review without inventing benchmark claims.

`docs/evidence.md` was deliberately left substantively unchanged in this pass because its versioned evidence/source contract was already internally coherent and there was no new health-reference decision. The file remains documented in the exhaustive reference and canonical ownership map.

### Release documentation completed

`docs/release.md` now treats documentation integrity as a release gate and requires:

- exact tracked-file reference coverage;
- documentation-map consistency;
- exact-head workflow evidence;
- current backup malformed/all-invalid checks;
- desktop split-height checks;
- exact eight-binary release assets and checksums;
- human/device/accessibility/signing/trust gates to remain separate from automation.

`.github/RELEASE_TEMPLATE.md` now contains matching documentation-integrity, backup, split-height, exact-head, artifact, signing and manual-acceptance checklist items.

### Project status documents reconciled

`CHANGELOG.md` now records:

- the exhaustive file reference/documentation map;
- `git ls-files` documentation enforcement;
- all-invalid Android restore hardening;
- desktop remaining-inch hardening;
- Python setup correction;
- expanded privacy/security/governance documentation.

`ROADMAP.md` now marks the documentation ownership map, exhaustive file-level reference and machine-checked tracked-file coverage complete, while keeping exact-head CI, physical Android, accessibility, screenshots, target-host smoke testing and signing/trust tasks open.

### Focused commits from the deep documentation pass

The documentation work remained granular. Focused commit messages include:

- `docs: add documentation ownership map`;
- `docs: document every tracked repository file`;
- `ci: require documentation for every tracked file`;
- `fix(ci): align backup invariant with implementation`;
- `docs: expose exhaustive documentation from readme`;
- `docs: require file-level documentation maintenance`;
- `docs: enforce documentation coverage in pull requests`;
- `docs: govern exhaustive repository documentation`;
- `docs: deepen module and documentation maintenance guide`;
- `docs: complete regression and documentation-integrity matrix`;
- `docs: complete architecture and data-boundary reference`;
- `docs: reconcile release guide with final integrity gates`;
- `docs: complete setup and verification prerequisites`;
- `docs: complete troubleshooting for current verification stack`;
- `docs: complete desktop behavior and packaging contract`;
- `docs: complete privacy contract for defensive restore`;
- `docs: deepen security threat and release boundaries`;
- `docs: record exhaustive documentation and late hardening`;
- `docs: reconcile roadmap with documentation completion`;
- `docs: add documentation integrity to release template`;
- `docs: complete accessibility acceptance guidance`;
- `docs: align performance guide with bounded data safeguards`;
- `docs: align design system with current interaction states`;
- this handoff commit.

### Documentation completeness definition after this pass

For this repository, “complete documentation” now means all of the following rather than only a long README:

1. every tracked file has an exact-path responsibility entry;
2. every detailed contract has an identified canonical document;
3. contributor/reviewer/release workflows tell maintainers when those documents must change;
4. repository automation rejects newly tracked undocumented files;
5. specialist docs agree on Android persistence, desktop ephemerality, adult-use scope, backup transaction behavior, supported targets and release artifacts;
6. project status documents distinguish completed repository work from unresolved manual/external release gates.

### Exact-head verification required after this documentation commit

This handoff update becomes the new PR #14 head. All earlier workflow results are superseded for merge/release-readiness purposes.

The exact next actions are:

1. fetch PR #14 and record this commit's exact head SHA;
2. inspect only workflow runs attached to that head;
3. require `CI` to pass the new `git ls-files` documentation invariant and Markdown-link audit;
4. require Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review and Secret Scan to pass on the same head;
5. inspect exact-head Android screenshot and Desktop host artifacts when workflows complete;
6. make no additional code/documentation change unless a concrete exact-head verification failure is found;
7. do not create `v0.1.0` until physical Android, manual accessibility/screenshot review, desktop target-host acceptance and protected signing/trust requirements are actually complete.
