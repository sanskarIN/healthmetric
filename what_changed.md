# HealthMetric — Final 2.0.12 Work Handoff

Last updated: 2026-08-19

## Repository and integration state

Repository:

`https://github.com/sanskarIN/healthmetric`

Primary branch:

`main`

Active integration branch:

`phase3/release-readiness-and-desktop-core`

Active integration pull request:

- PR #14 — `release: prepare HealthMetric 2.0.12`
- `https://github.com/sanskarIN/healthmetric/pull/14`

PR #14 is the authoritative integration path for the current HealthMetric milestone. It combines the desktop/release-readiness work with the meaningful Android hardening/evidence work that had been developed separately in PR #13.

PR #13 (`fix: harden release evidence and final audit`) must not be merged independently after PR #14. After PR #14 is merged into `main`, compare PR #13 against merged `main`; close PR #13 as superseded only when that comparison proves no meaningful unique work remains.

This file is the authoritative continuation checkpoint for **HealthMetric 2.0.12**. Earlier handoff references to the planned `0.1.0` development release are superseded by this document.

## Release target

Prepared public version:

`2.0.12`

Prepared stable tag:

`v2.0.12`

Configured Android metadata:

- `versionName = "2.0.12"`
- `versionCode = 20012`

Configured desktop metadata:

- project `version = "2.0.12"`
- native `packageVersion = "2.0.12"`
- Linux DEB metadata uses `2.0.12`
- Windows MSI metadata uses `2.0.12`
- macOS DMG metadata uses `2.0.12`

Android version-code mapping:

`MAJOR * 10000 + MINOR * 100 + PATCH`

For `2.0.12`:

`2 * 10000 + 0 * 100 + 12 = 20012`

The mapping reserves two decimal digits for `MINOR` and two for `PATCH`. Release validation rejects versions that cannot be represented by this mapping.

The previous desktop `0.1.0` development configuration used a macOS-only native package version `1.0.0` because the packaging tool required a positive major component. That workaround has been removed. The `2.0.12` release candidate uses the same version across the desktop project and all native package metadata.

Do not create `v2.0.12` merely because the version fields are configured. The exact-head automation and documented manual/device/signing gates remain mandatory.

## Repository foundation completed

Implemented and retained:

- public MIT-licensed repository structure;
- Kotlin/Android/Kotlin Multiplatform Gradle build;
- central dependency/version catalog;
- Kotlin formatting/lint integration;
- contributor documentation;
- Code of Conduct;
- security policy;
- privacy policy;
- support documentation;
- issue templates;
- pull-request template;
- release template;
- architecture decision records;
- Dependabot;
- CI workflow;
- Android instrumentation workflow;
- desktop Linux/Windows/macOS workflow;
- Apple shared-core workflow;
- CodeQL workflow;
- Dependency Review workflow;
- full-history Secret Scan workflow;
- tagged release workflow;
- local Unix verification script;
- local PowerShell verification script;
- repository invariant audit;
- internal Markdown-link audit;
- Python release-tooling regression suite;
- canonical documentation ownership map;
- exhaustive tracked-file responsibility reference.

## Shared health domain completed

The `shared` Kotlin Multiplatform module remains the authoritative calculation/validation layer.

Implemented:

- adult metric BMI calculation;
- adult imperial BMI calculation;
- adult waist-to-height calculation;
- metric/imperial unit conversions;
- strict finite-number validation;
- plausible adult measurement bounds;
- versioned adult BMI reference profile;
- evidence publisher/title/URL/note metadata;
- explicit evidence review date metadata;
- neutral educational result notices;
- deterministic unit tests;
- boundary tests;
- deterministic property-style tests;
- Android target;
- JVM/Desktop target;
- iOS device target;
- iOS simulator target.

The Android and desktop presentation layers must not silently duplicate or change shared adult health thresholds.

### Imperial validation corrections

The final domain audit fixed unit-contract problems in imperial flows.

Completed:

- imperial weight validation reports pound limits rather than kilogram limits;
- imperial waist validation reports inch limits rather than centimeter limits;
- imperial BMI no longer validates the imperial value and then incorrectly revalidates converted values through metric boundaries;
- documented imperial BMI boundary values remain valid after conversion;
- imperial waist-to-height preserves its imperial validation contract rather than reusing metric validation after conversion;
- metric and imperial error messages remain unit-specific.

Regression coverage locks:

- imperial pound error wording;
- imperial inch error wording;
- documented lower/upper imperial BMI boundaries;
- documented lower/upper imperial waist/height boundaries;
- desktop presentation of shared imperial validation errors.

## Android adult-use safety boundary completed

Implemented:

- first-run adult-use notice;
- explicit `I am 18 or older` action;
- explicit `I am under 18` action;
- under-18 path blocks adult BMI/waist reference results;
- adult-use confirmation remains device-local state;
- portable backup import cannot set adult-use confirmation;
- portable backup import cannot set onboarding completion;
- neutral wording avoids diagnosis claims, appearance rankings, or personal body targets.

### Recoverable age selection

An accidental under-18 selection previously persisted a blocked state without an in-app correction path.

Fixed with:

- `HealthMetricDataStore.resetAdultUseChoice()`;
- ViewModel exposure;
- `Return to age selection` action;
- stable Compose test tags for age-gate controls;
- UI instrumentation coverage;
- DataStore regression coverage proving the reset does not clear unrelated history consent, history entries, retention, or theme settings.

The safety gate remains fail-closed: adult calculators are available only after the adult choice is explicitly selected.

## Android privacy-first history completed

Android history remains optional and **disabled by default**.

Persisted history stores only:

- local identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not persisted in calculation history.

Supported retention limits:

- 50;
- 100;
- 250;
- 500.

Default retention:

`100`

Hard maximum:

`500`

Lowering retention immediately removes older entries beyond the newly selected cap.

### Canonical chronology

All Android history paths now use the same invariant:

1. sanitize accepted entries;
2. deduplicate IDs;
3. sort by `timestampEpochMillis` descending;
4. apply the retention limit.

This applies to:

- newly recorded calculations;
- imported backups;
- Undo restoration of an individually deleted entry.

This fixes the prior defect where Undo could move an older entry to the top and prevents arbitrary backup JSON array order from deciding which records survive retention.

### History identifiers

New locally recorded Android history entries use:

`UUID.randomUUID().toString()`

Imported schema-v1 identifiers remain backward compatible but must be:

- trimmed;
- non-blank;
- at most 96 characters;
- deduplicated after sanitation.

### Individual deletion and Undo

Implemented:

- per-entry delete action;
- accessible delete semantics;
- persisted deletion before feedback;
- snackbar Undo;
- chronology-preserving restoration;
- Undo does not enable future history saving.

Confirmed erase-all remains a separate destructive action.

## Android backup/export completed and hardened

Settings provides:

- `Save JSON backup to a file`;
- `Share JSON backup`;
- `Restore from JSON backup`.

Platform behavior:

- file save uses Android Storage Access Framework `CreateDocument`;
- share export uses an explicit chooser;
- restore reads a selected document and requires confirmation before persistence mutation.

Current portable schema:

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

Maximum backup size:

`1,048,576 bytes` (1 MiB)

### Backup safety protections

Implemented:

- bounded streaming read;
- bounded write;
- strict well-formed UTF-8 decoding;
- independent restore-boundary UTF-8 size check;
- top-level JSON parsing;
- supported schema-version validation;
- required top-level `history` array validation before DataStore mutation;
- explicit empty-history support through `history: []`;
- rejection of non-empty history arrays when zero valid records survive sanitation;
- supported retention normalization;
- theme normalization;
- malformed individual record recovery when valid neighboring records survive;
- bounded/non-blank identifiers;
- non-negative timestamps;
- finite stored values;
- known calculator types only;
- bounded summaries;
- duplicate-ID removal;
- newest-first chronology normalization;
- retention after chronology normalization;
- preservation of device-local consent/adult-gate state.

### Strict UTF-8 correction

A final audit found that ordinary Java UTF-8 string decoding could replace malformed byte sequences with the Unicode replacement character instead of failing.

Fixed:

- `BackupIo.readUtf8` now uses a UTF-8 decoder configured with `CodingErrorAction.REPORT`;
- malformed input fails before restore confirmation can accept the payload as valid text;
- unmappable input fails closed;
- valid UTF-8 round trips remain unchanged;
- the 1 MiB limit remains unchanged.

Regression coverage includes a deliberately malformed UTF-8 byte sequence that must raise a character-coding failure.

Authoritative backup contract:

`docs/backup-format.md`

Architecture decision:

`docs/adr/0004-bounded-user-controlled-local-data.md`

## Android navigation/state hardening completed

### About navigation

The About destination previously hid bottom navigation without a reliable in-app return path.

Fixed with:

- explicit top-app-bar Back action;
- previous-destination tracking;
- Android system Back handling;
- stable About open/back test tags;
- stable bottom-navigation test tags;
- `AboutNavigationUiTest` coverage returning to BMI and Settings origins.

### Saved-state enum safety

Direct enum restoration from `rememberSaveable` strings could crash after future enum rename/removal.

Added:

- `savedEnumValueOrDefault` helper;
- known/stale/empty value regression tests;
- safe main navigation restoration;
- safe previous-About-origin restoration;
- safe history-filter restoration.

Unknown saved enum names now fall back safely.

## Android chart hardening completed

Imported schema-v1 history permits finite stored values. Extreme finite values could make ordinary range subtraction overflow to infinity.

Added:

- `ChartScale.kt`;
- scale-first normalization using maximum absolute finite magnitude;
- flat-series centering;
- explicit non-finite rejection;
- tests including `-Double.MAX_VALUE`, zero, and `Double.MAX_VALUE`;
- `HistoryScreen` integration using normalized chart coordinates.

## Android release evidence completed

`ReleaseScreenshotCaptureTest` drives the real Android application and captures eight required fictional/example screenshots:

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
- pulls the app-scoped screenshot directory;
- publishes `android-release-screenshots`;
- fails artifact upload when required PNG evidence is missing;
- separately publishes instrumentation reports.

Automated screenshot generation is not final marketing approval. Human visual/privacy review remains a release gate.

## Desktop client completed

PR #14 includes a Compose Multiplatform desktop application in `desktopApp`.

Implemented journeys:

- explicit adult-use gate;
- separate under-18 unavailable path;
- metric adult BMI;
- imperial adult BMI;
- metric adult waist-to-height;
- imperial adult waist-to-height;
- dot/comma decimal input;
- shared-domain validation/reference behavior;
- neutral educational result presentation;
- light/dark session theme;
- About/evidence/project/support/funding information;
- explicit external-link actions.

### Desktop privacy model

Desktop intentionally has no HealthMetric persistence layer.

It does not persist:

- weight/height/waist inputs;
- BMI/ratio results;
- adult-use selection;
- theme selection;
- calculator/navigation state.

Those values live only in process memory and are discarded when the application closes.

Desktop does not currently import/export Android backup files.

Architecture decision:

`docs/adr/0005-ephemeral-desktop-client.md`

Desktop guide:

`docs/desktop.md`

### Desktop input hardening

`DesktopNumbers` accepts ordinary measurement number syntax only.

Accepted examples:

- `72`;
- `72.5`;
- `72,5`;
- `.5`;
- `72.`;
- surrounding whitespace.

Rejected examples:

- scientific notation (`1e2`);
- explicit signs (`+72`, `-72`);
- `NaN`;
- infinity;
- malformed mixed separators;
- non-whole feet.

Imperial split height means **whole feet + remaining inches**. Remaining inches must be in `[0, 12)`.

Examples rejected before total-height calculation:

- `12` remaining inches;
- `12.0` remaining inches;
- `20` remaining inches.

### Desktop packaging

Configured:

- current-OS runnable JAR;
- Linux DEB;
- Windows MSI;
- macOS DMG.

Host-specific tasks:

- `:desktopApp:packageUberJarForCurrentOS`;
- Linux `:desktopApp:packageDeb`;
- Windows `:desktopApp:packageMsi`;
- macOS `:desktopApp:packageDmg`.

Current `2.0.12` metadata is uniform across project/native package configuration.

The dedicated Desktop workflow verifies formatting, tests, JAR creation, and the matching native installer on Linux, Windows, and macOS.

Native build success is not production signing/notarization approval. Human install/uninstall/accessibility/smoke testing remains required.

## Apple shared core completed

The shared module includes:

- `iosArm64`;
- `iosSimulatorArm64`.

`.github/workflows/apple-shared.yml` compiles both on macOS and verifies shared JVM tests.

No iOS user interface is claimed.

## Release integrity tooling completed

### Stable release validation

`scripts/check_release_version.py` now validates:

- stable `vMAJOR.MINOR.PATCH` tag form;
- Android `versionName`;
- Android `versionCode`;
- Android semantic version-code mapping;
- desktop project `version`;
- desktop native `packageVersion`;
- agreement of all public/native version values;
- proposed tag agreement.

For the current candidate, tests lock:

- Android `versionName = 2.0.12`;
- Android `versionCode = 20012`;
- desktop `version = 2.0.12`;
- desktop `packageVersion = 2.0.12`;
- tag `v2.0.12`.

The tagged release workflow also requires the tag commit to equal current `main`.

### Least-privilege release workflow

`.github/workflows/release.yml` defaults to:

`contents: read`

Only the final publish job receives:

`contents: write`

Build/verification jobs do not receive repository write permission.

### Deterministic staging

`scripts/stage_release_assets.py` requires exactly one non-empty expected build output per artifact type.

For `v2.0.12`, expected staged binaries are:

- `healthmetric-v2.0.12-android-unsigned.apk`;
- `healthmetric-v2.0.12-android-unsigned.aab`;
- `healthmetric-v2.0.12-desktop-linux.jar`;
- `healthmetric-v2.0.12-desktop-linux.deb`;
- `healthmetric-v2.0.12-desktop-windows.jar`;
- `healthmetric-v2.0.12-desktop-windows.msi`;
- `healthmetric-v2.0.12-desktop-macos.jar`;
- `healthmetric-v2.0.12-desktop-macos.dmg`.

Zero matches, duplicate matches, empty files, unsupported platforms, and invalid tags fail closed.

### Final asset verification

`scripts/verify_release_assets.py` requires exactly the expected eight binaries and rejects:

- missing assets;
- unexpected assets;
- empty assets.

It generates:

`SHA256SUMS.txt`

The final GitHub release command uses `--verify-tag`.

## Repository-tooling tests completed

Python tests under `scripts/tests/` cover:

- stable tag syntax;
- current 2.0.12 release metadata;
- Android semantic version-code mapping;
- rejection of too-large minor/patch components for the mapping;
- Android/desktop/native package version agreement;
- deterministic Android staging;
- deterministic Linux/Windows/macOS staging;
- duplicate/empty output rejection;
- exact final asset-set verification;
- missing/extra/empty asset rejection;
- deterministic SHA-256 manifest output.

They run in:

- main CI;
- tagged release preflight;
- `scripts/verify.sh`;
- `scripts/verify.ps1`.

## Repository invariant audit completed

`scripts/check_repository.py` machine-checks durable repository requirements including:

- required project/community/documentation paths;
- Android manifest contains no Internet permission;
- Android application backup remains disabled;
- Android cleartext traffic remains disabled;
- required Android backup top-level history structure guard;
- Android chart finite-safe normalization integration;
- Android adult-use correction path;
- saved-enum fallback helper/tests;
- desktop module inclusion;
- desktop dependency on shared;
- desktop native distribution formats;
- required adult/privacy desktop copy;
- README metadata;
- privacy documentation invariants;
- Android AAB tasks/artifacts;
- Android screenshot evidence configuration;
- all eight screenshot names;
- desktop workflow tests/JAR/native packaging;
- release tag/version/main/staging/checksum/permission invariants;
- accidental `docs/.noop-probe` prohibition;
- exhaustive tracked-file documentation coverage.

## Documentation system completed

### Canonical ownership map

`docs/documentation-map.md` defines:

- documentation principles;
- audience entry points;
- canonical document ownership by topic;
- change-type → document-review/update matrix;
- ADR policy;
- rules against overstating CI, screenshots, signing, iOS UI, persistence, or medical meaning;
- tracked-file documentation maintenance procedure.

### Exhaustive tracked-file reference

`docs/repository-file-reference.md` documents every tracked repository file by exact path and responsibility, including:

- root metadata/configuration/community files;
- GitHub templates/workflows;
- Android build/manifest/source/resources/tests;
- shared source/tests;
- desktop source/tests;
- Gradle version catalog;
- Python/shell/PowerShell tooling;
- tooling tests;
- project documentation;
- ADRs;
- logo and screenshot policy assets.

`scripts/check_repository.py` runs `git ls-files -z` and requires every tracked path to appear exactly in that reference.

A newly tracked undocumented file therefore fails verification.

### 2.0.12 documentation reconciliation

The following release-facing documents have been aligned to `2.0.12`:

- `README.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `docs/release.md`;
- `docs/testing.md`;
- `docs/desktop.md`;
- `.github/RELEASE_TEMPLATE.md`;
- PR #14 title/body;
- this `what_changed.md`.

`docs/backup-format.md` already documents the strict well-formed UTF-8 boundary and fail-closed malformed byte behavior.

## Current verification commands

Unix-like complete non-device suite:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Current release-version validation:

```bash
python3 scripts/check_release_version.py v2.0.12
```

Repository/docs checks:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
```

Shared/Desktop/Android core checks:

```bash
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

Android connected tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

Apple shared targets on macOS:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Native desktop packages:

```bash
# Linux
gradle :desktopApp:packageDeb

# Windows
gradle :desktopApp:packageMsi

# macOS
gradle :desktopApp:packageDmg
```

## Commit strategy

The project continuation deliberately uses meaningful granular Conventional Commits instead of one giant commit or empty/churn commits.

Major commit groups already present in the integration history include:

- Android chronology fixes;
- UUID history identifiers;
- About navigation fixes/tests;
- Android screenshot capture/evidence workflow;
- backup structure hardening/tests;
- adult-gate correction state/tests;
- saved-state enum hardening/tests;
- chart overflow hardening/tests;
- imperial validation corrections/tests;
- desktop parser/split-height hardening/tests;
- deterministic release staging/tests;
- exact release asset verification/checksum tests;
- release permission/tag/main hardening;
- documentation ownership/exhaustive file-reference work;
- deep repository documentation reconciliation.

Focused commits added in the final UTF-8/2.0.12 passes include:

- `fix: reject malformed UTF-8 backup input`;
- `test: cover malformed UTF-8 backup rejection`;
- `fix: use byte literals in malformed backup test`;
- `docs: document strict UTF-8 backup decoding`;
- `docs: record strict backup encoding rejection`;
- `docs: record final strict backup encoding audit`;
- `release(android): set version 2.0.12`;
- `release(desktop): set version 2.0.12`;
- `release: validate Android semantic version code mapping`;
- `test(release): lock version 2.0.12 code mapping`;
- `release: validate desktop native package version`;
- `test(release): verify all 2.0.12 platform versions`;
- `docs: target HealthMetric 2.0.12 in roadmap`;
- `docs: prepare changelog for version 2.0.12`;
- `docs: prepare release guide for 2.0.12`;
- `docs: align release template with 2.0.12`;
- `docs: align desktop packaging with 2.0.12`;
- `docs: test version 2.0.12 release consistency`;
- `docs: publish 2.0.12 release target in readme`;
- this final handoff commit.

Git history remains authoritative for exact commit SHAs.

Requested commit email used by the project work:

`sanskarin@outlook.in`

## What remains before merging PR #14

The implementation/repository work is intended to be complete subject to exact-head automation.

Do not merge using workflow results from an older commit.

The exact final PR #14 head must pass:

- CI;
- Android instrumentation;
- Desktop;
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

Also verify exact-head artifacts:

- `android-release-screenshots` contains all eight required PNGs;
- Linux desktop verification contains runnable JAR + DEB;
- Windows desktop verification contains runnable JAR + MSI;
- macOS desktop verification contains runnable JAR + DMG.

If a workflow actually fails, inspect the exact failed job/log and make the smallest root-cause fix with regression or invariant coverage where appropriate.

Queued/pending GitHub runner state is not itself evidence of a source failure and must not be represented as a passed check either.

## What remains before public v2.0.12

These tasks require real hardware, human review, protected credentials, or external trust systems and therefore are intentionally not falsely marked complete.

### Android physical/device acceptance

Required:

- physical Android release-candidate smoke testing;
- primary calculator flows;
- adult gate and correction path;
- history disabled/enabled behavior;
- retention changes;
- per-entry delete/Undo;
- erase-all confirmation;
- file backup;
- share backup;
- restore confirmation;
- malformed/unsupported/oversized backup rejection;
- delete-all-data behavior;
- theme behavior;
- release/update link;
- About navigation.

### Android accessibility/visual acceptance

Required:

- TalkBack review;
- maximum-font review;
- display scaling review;
- keyboard/DPAD review where applicable;
- light/dark/dynamic theme review;
- chart accessibility review;
- destructive-dialog accessibility review;
- human visual/privacy approval of exact CI-generated screenshots.

### Desktop host acceptance

Required on every published desktop platform:

- runnable JAR launch;
- native installer install/launch/uninstall;
- adult gate;
- metric/imperial BMI;
- metric/imperial waist-to-height;
- split remaining-inch validation;
- process-restart ephemerality;
- keyboard/focus behavior;
- display scaling;
- screen-reader naming where available;
- external-link behavior;
- platform warning/signing/notarization behavior.

### Protected signing/trust setup

Still external to source control:

- Android production signing / Play App Signing;
- Android signing secrets/passwords;
- desktop signing certificates/private keys if signed installers are distributed;
- macOS notarization credentials if notarized DMGs are distributed.

No signing secret, certificate private key, password, or notarization credential should be committed.

## Merge and release sequence

1. Fetch PR #14 and record its exact final head SHA after this handoff commit.
2. Inspect only workflow runs attached to that exact SHA.
3. Require CI, Android instrumentation, Desktop, Apple shared core, CodeQL, Dependency Review, and Secret Scan to succeed.
4. Inspect the exact-head Android screenshot artifact.
5. Inspect the exact-head desktop host artifacts.
6. Fix only concrete exact-head failures.
7. Merge PR #14 into `main` with normal history-preserving merge behavior only after required exact-head automation is green.
8. Inspect post-merge `main` automation.
9. Compare PR #13 against merged `main` and close PR #13 as superseded only if no meaningful unique change remains.
10. Complete physical Android, accessibility/visual, desktop host, and protected signing/trust gates.
11. Re-run/confirm `python3 scripts/check_release_version.py v2.0.12` on the exact release commit.
12. Confirm `CHANGELOG.md`, `ROADMAP.md`, `docs/release.md`, and this handoff match the exact release commit.
13. Create annotated tag only after every blocker is closed:

```bash
git tag -a v2.0.12 -m "HealthMetric v2.0.12"
git push origin v2.0.12
```

14. Allow the tagged release workflow to independently run preflight, build Android/desktop assets, verify the exact eight-binary set, generate checksums, verify the tag, and create the GitHub Release.
15. Do not rewrite the published tag to hide a later defect; fix on `main` and publish a new patch release.

## Final continuation rule

After this `what_changed.md` commit, make no additional source/documentation change merely to increase commit count.

Only continue changing the branch when one of these is true:

- exact-head automation reports a concrete failure;
- a real remaining correctness/security/privacy/accessibility/release-integrity defect is found;
- the user explicitly requests another functional/version change.

Every new commit supersedes older exact-head workflow evidence and therefore requires verification on the new SHA.
