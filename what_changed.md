# HealthMetric — Work Handoff

Last updated: 2026-08-19

## Current repository state

Repository:

`https://github.com/sanskarIN/healthmetric`

Default/integration branch:

`main`

Previous implementation milestone:

- PR #12 — `feat: complete privacy data controls and release verification`
- status: merged
- merge commit: `4e1481839deb3a069f640dd2092ab47173a0a75a`

Current continuation branch:

`phase3/release-hardening-final-audit`

Current continuation pull request:

- PR #13 — `fix: harden release evidence and final audit`
- `https://github.com/sanskarIN/healthmetric/pull/13`
- base: `main`
- base commit: `4e1481839deb3a069f640dd2092ab47173a0a75a`

This continuation is a release-hardening/final-audit pass. It does not add unrelated health features merely to increase feature count. The work concentrates on correctness, regression prevention, reproducible Android artifacts, real-app release evidence, documentation truthfulness, and removal of stale/accidental repository state.

## Important defects discovered during this continuation

### Accidental repository probe file

A temporary connector/write probe named:

`docs/.noop-probe`

had accidentally reached `main` through the previous milestone.

It was removed immediately in a dedicated commit. `scripts/check_repository.py` now rejects that forbidden path so the same accidental file cannot silently return.

### History delete/undo chronology defect

`restoreHistoryEntry()` previously inserted an undone entry at the front of history regardless of its original timestamp. Deleting an older record and selecting Undo could therefore make the old record appear newest.

The persistence layer now:

1. sanitizes the candidate record;
2. removes any existing record with the same normalized ID;
3. adds the record to the candidate set;
4. sorts by `timestampEpochMillis` descending;
5. applies the configured retention cap after sorting.

The same canonical newest-first rule is used when adding new history.

A dedicated instrumentation regression test verifies that deleting and restoring the middle item from a `newest → middle → oldest` sequence returns it to the middle rather than the top.

### Imported-history ordering defect

Backup restore previously depended on the serialized JSON array order before applying retention. A valid but out-of-order backup could therefore preserve the first encountered records instead of the chronologically newest records.

`decodeHistory()` now:

1. parses the bounded JSON array;
2. sanitizes each record independently;
3. skips invalid records;
4. retains only the first valid record for a normalized duplicate ID;
5. sorts the complete accepted bounded set by timestamp descending;
6. applies the application-wide maximum after sorting;
7. applies the selected retention limit at restore.

The 1 MiB whole-payload limit remains the primary complexity/input-size boundary, so HealthMetric does not need to stop after the first 500 valid serialized records and accidentally discard newer valid records appearing later in the document.

### About navigation trap

About hides bottom navigation. The app previously provided no in-app way back after opening About, so a user could be forced to leave/restart the activity to recover navigation.

HealthMetric now records the screen that opened About and provides:

- an explicit top-app-bar Back action;
- Android system-back handling while About is active;
- return to the originating app destination;
- a defensive fallback to the BMI screen if the saved origin is invalid.

A full-app instrumentation regression test verifies About → Back from both the BMI and Settings origins.

### Avoidable local history ID collision path

New records previously combined the current timestamp with a small random numeric suffix. Although collisions were unlikely, a collision could replace an existing same-ID history record.

New locally recorded entries now use:

`UUID.randomUUID().toString()`

Imported schema-v1 IDs remain backward-compatible: they do not have to be UUIDs, but they are still trimmed, bounded, validated, and deduplicated at the persistence boundary.

### Release App Bundle documentation drift

Earlier documentation/roadmap text said unsigned Android App Bundle packaging was implemented, but the actual main CI workflow, tagged release workflow, Unix verifier, and Windows verifier were still APK-only.

This continuation corrected the implementation rather than weakening the documentation claim.

The actual pipeline now runs:

`gradle :androidApp:bundleRelease`

and handles:

- debug APK;
- unsigned release APK;
- unsigned release AAB.

The main CI uploads the expected AAB artifact. The tagged release workflow builds and attaches both unsigned release APK and AAB artifacts. Unix and PowerShell verification scripts include `bundleRelease`.

Repository invariants now verify those task/artifact references so future documentation cannot silently outrun the implementation in the same way.

## Product/data hardening

### Canonical history order

History is now canonical newest-first by `timestampEpochMillis` across:

- new calculations;
- replacing an entry with the same ID;
- imported valid backup history;
- delete/undo restoration.

Retention is applied after chronological normalization.

### History identifiers

New locally recorded history entries use UUID identifiers.

Imported/programmatic identifiers remain subject to the existing persistence contract:

- trim surrounding whitespace;
- reject blank identifiers;
- cap identifier length to 96 characters;
- deduplicate normalized IDs before persistence/display.

### Retention

Existing supported limits remain:

- 50;
- 100;
- 250;
- 500.

Default remains 100. Application-wide maximum remains 500.

Lowering the setting still immediately trims older data. Because history is canonical newest-first, the retained subset is the newest valid subset rather than an insertion-order subset.

### Portable backup behavior

Backup schema remains version `1`.

Portable fields remain:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- `history`.

Device-local/non-portable state remains:

- current history-saving consent;
- adult-use confirmation;
- onboarding completion.

Restore still cannot silently enable future history saving or change adult-only/onboarding state.

Backup IO remains bounded to 1 MiB of UTF-8 data before JSON processing/writing.

## Navigation/accessibility hardening

Added the string resource:

`navigate_back = Back`

About now exposes a standalone back icon with an accessible content description.

Stable UI automation semantics were expanded to include:

- `NAV_BMI`;
- `NAV_WAIST`;
- `NAV_HISTORY`;
- `NAV_SETTINGS`;
- `ABOUT_OPEN`;
- `ABOUT_BACK`;
- existing BMI/waist/history/settings critical controls.

These tags support deterministic automation and do not replace user-facing accessibility labels.

## New full-app navigation regression coverage

Added `AboutNavigationUiTest`.

It:

- runs the real `MainActivity`;
- clears local DataStore state before and after execution;
- completes the adult onboarding path for the test fixture;
- opens About from BMI;
- verifies explicit Back returns to BMI;
- navigates to Settings;
- opens About from Settings;
- verifies explicit Back returns to Settings.

The state reset prevents instrumentation execution order from changing the test's starting screen.

## Automated real-app release screenshot evidence

Added:

`androidApp/src/androidTest/java/io/github/sanskarin/healthmetric/ReleaseScreenshotCaptureTest.kt`

This is not a mock screenshot generator. It runs the actual Android app through `MainActivity` and captures the emulator display through Android `UiAutomation`.

The test resets local app state before capture and uses fictional/example fixture values only.

Required generated PNG set:

1. `01-onboarding.png`
2. `02-bmi-metric.png`
3. `03-bmi-result.png`
4. `04-waist-ratio.png`
5. `05-history.png`
6. `06-settings.png`
7. `07-about.png`
8. `08-dark-theme.png`

The journey covers:

- adult-use onboarding notice;
- metric BMI calculator;
- neutral BMI result;
- privacy/history opt-in;
- waist-to-height result;
- persisted local history/chart;
- About/support presentation;
- dark theme.

Each bitmap is encoded directly to app-scoped external storage and recycled after writing instead of retaining the full set in memory.

## Android instrumentation workflow evidence pipeline

`.github/workflows/android-instrumentation.yml` now:

- provisions the API 35 Google APIs x86_64 Pixel 7 emulator profile;
- runs `gradle :androidApp:connectedDebugAndroidTest`;
- pulls the app-scoped `release-screenshots` directory with `adb`;
- uploads the PNG set as `android-release-screenshots`;
- treats missing screenshot files as an artifact-upload error;
- continues uploading instrumentation test reports.

This changes screenshots from a manual-only placeholder into reproducible release evidence.

CI-generated screenshots still require human visual/privacy review before permanent README/store publication. Automated generation does not pretend to replace physical-device or manual accessibility review.

## Main CI release packaging

`.github/workflows/ci.yml` now performs:

1. repository invariant audit;
2. internal Markdown-link audit;
3. JDK 17 setup;
4. Gradle 8.13 setup;
5. Android SDK Platform 36 / Build Tools 35.0.0 setup;
6. shared + Android ktlint checks;
7. shared JVM tests;
8. Android JVM tests;
9. Android release lint;
10. debug APK assembly;
11. unsigned release APK assembly;
12. unsigned release App Bundle assembly;
13. lint report upload;
14. debug APK upload;
15. unsigned release APK upload;
16. unsigned release AAB upload.

Expected AAB location:

`androidApp/build/outputs/bundle/release/*.aab`

## Tagged release workflow

`.github/workflows/release.yml` now:

- runs shared/Android test, formatting, and release-lint prerequisites;
- assembles the unsigned release APK;
- assembles the unsigned release AAB;
- uploads both as a workflow artifact;
- attaches both unsigned artifacts to the generated GitHub Release.

The repository still intentionally does not contain production Android signing material.

Production/store signing remains a protected distribution-environment responsibility.

## Reproducible local verification scripts

`scripts/verify.sh` now runs:

- shared + Android ktlint;
- shared JVM tests;
- Android JVM tests;
- Android release lint;
- debug APK assembly;
- unsigned release APK assembly;
- unsigned release App Bundle assembly.

`scripts/verify.ps1` runs the same sequence with explicit PowerShell exit-code checks after every Gradle invocation.

`GRADLE_BIN` remains available to override the Gradle executable path/name.

## Repository invariant hardening

`scripts/check_repository.py` now verifies more than file presence and manifest privacy defaults.

It verifies:

- the full required repository/documentation/workflow set exists;
- `docs/.noop-probe` is absent;
- Android manifest does not request Internet permission;
- Android application backup remains disabled;
- cleartext traffic remains disabled;
- README retains required credit/funding/contact/license metadata;
- privacy documentation retains key default/adult/backup invariants;
- main CI contains `bundleRelease` and the expected AAB artifact path;
- tagged release workflow contains `bundleRelease` and the expected AAB release path;
- Unix and Windows local verification include `bundleRelease`;
- Android instrumentation workflow contains screenshot pull/artifact configuration;
- the release screenshot test contains all eight required PNG names.

Internal Markdown targets continue to be checked by `scripts/check_markdown_links.py` without external network requests.

## Documentation truthfulness audit

The continuation performed a repository-wide source/config/test/documentation audit and corrected documentation where claims were stale or ahead of implementation.

### README

Updated to document only implemented behavior:

- canonical history ordering;
- UUID new-record identifiers;
- About back navigation;
- real-app CI screenshot evidence;
- debug APK + unsigned release APK + unsigned AAB packaging;
- current verification commands;
- generated screenshot artifact review model.

### CHANGELOG

Updated Unreleased entries with:

- release-hardening additions;
- chronology fix;
- About-navigation fix;
- UUID change;
- accidental probe removal;
- corrected AAB implementation drift;
- screenshot evidence automation;
- remaining manual/release gates.

### ROADMAP

Updated implemented milestones for:

- canonical chronology;
- UUID IDs;
- About return navigation;
- deterministic real-app screenshot capture implementation;
- AAB CI/release/local verification;
- repository invariant hardening.

The roadmap intentionally does **not** mark the following complete before evidence exists:

- exact PR #13 green check group;
- exact release-candidate screenshot artifact success/visual approval;
- physical Android hardware review;
- manual TalkBack/large-font/device accessibility review;
- protected production signing;
- final `v0.1.0` release audit/tag.

### Testing guide

Updated with:

- About navigation test;
- chronology regression invariants;
- UUID behavior;
- screenshot capture test and eight-file evidence set;
- stable navigation tags;
- AAB CI gates;
- manual accessibility review boundary.

### Screenshot guide

Replaced the manual-only placeholder with the actual emulator evidence workflow, required PNG set, artifact behavior, human visual/privacy approval requirements, and rules for optional permanent publication.

### Release guide

Updated release checklist requires:

- repository/link audits;
- APK and AAB builds;
- connected instrumentation;
- screenshot artifact inspection;
- chronology and delete/undo verification;
- About return navigation;
- Apple shared-core compilation;
- security workflows;
- manual accessibility/device review;
- protected signing outside Git.

### Backup format

Updated schema documentation with:

- UUID example for new local records;
- backward-compatible imported-ID rules;
- deduplication then timestamp-descending sort;
- retention applied after chronological normalization;
- explicit explanation that serialized array order is not authoritative chronology.

### Architecture

Updated architecture documentation with:

- UUID generation boundary;
- chronological DataStore contract;
- About origin/back state;
- screenshot evidence boundary;
- main CI APK+AAB build boundary;
- emulator screenshot artifact boundary.

### Accessibility

Updated with:

- About back-label behavior;
- navigation return checks;
- automated screenshot evidence scope;
- continued manual TalkBack/large-font/theme/DPAD/physical-device requirements.

### Development guide

Updated developer invariants for:

- UUID IDs;
- canonical history ordering before retention;
- About navigation recoverability;
- release screenshot test maintenance;
- APK+AAB local/release builds.

### Performance guide

Corrected a stale statement that restore could stop after the first application maximum of valid serialized records.

The documented implementation now matches the code:

- whole input is bounded to 1 MiB;
- accepted records are validated/deduplicated;
- accepted records are sorted by timestamp;
- retention is then applied.

This prevents serialization order from controlling which recent records survive.

### ADR 0004

Expanded the bounded-local-data decision with:

- canonical chronology as a data contract;
- retention-after-sort behavior;
- UUID IDs for new local history;
- imported-ID backward compatibility;
- delete/undo chronology;
- rationale for processing the complete bounded accepted set before capping.

### Troubleshooting

Added guidance for:

- repository invariant failures;
- About navigation expectations;
- chronological history expectations;
- missing AAB outputs;
- emulator screenshot evidence failures;
- missing screenshot artifacts.

The guide explicitly says not to replace missing real-app screenshot evidence with fabricated/mock screenshots.

### Security policy

Release security gates now explicitly require:

- repository/link audits;
- unit/lint checks;
- debug APK;
- unsigned release APK;
- unsigned release AAB;
- Android connected tests and screenshot artifact;
- Apple shared-core compilation;
- CodeQL;
- dependency review where applicable;
- full-history secret scan.

The imported-data threat model now documents newest-first ordering before retention.

### Privacy policy

Updated with:

- UUID IDs for new local history;
- canonical newest-first ordering;
- delete/undo chronological behavior;
- chronological restore before retention;
- emulator screenshot evidence as test-only repository behavior, not product telemetry.

### Contribution and repository templates

`CONTRIBUTING.md`, `.github/PULL_REQUEST_TEMPLATE.md`, and `.github/RELEASE_TEMPLATE.md` now require/check:

- chronology invariants;
- collision-resistant local IDs;
- APK/AAB builds;
- screenshot evidence maintenance/review;
- secondary-destination back navigation;
- repository audits;
- protected signing boundaries.

## Source/test files audited in this continuation

Production Android source inspected:

- `MainActivity.kt`;
- `AppModels.kt`;
- `BackupIo.kt`;
- `HealthMetricDataStore.kt`;
- `SafeLogger.kt`;
- `HealthMetricViewModel.kt`;
- `HealthMetricApp.kt`;
- `LocalizedNumbers.kt`;
- `MeasurementNumberField.kt`;
- `CalculatorScreen.kt`;
- `WaistToHeightScreen.kt`;
- `HistoryScreen.kt`;
- `SettingsScreen.kt`;
- `AboutScreen.kt`;
- `OnboardingScreen.kt`;
- `HealthMetricTestTags.kt`;
- `DesignTokens.kt`;
- `Theme.kt`.

Shared domain inspected:

- `Bmi.kt`;
- `Validation.kt`;
- `Units.kt`;
- `WaistToHeight.kt`;
- shared module Gradle configuration.

Android JVM tests inspected:

- `AppPreferencesTest.kt`;
- `BackupIoTest.kt`;
- `LocalizedNumbersTest.kt`.

Android instrumentation tests inspected:

- `OnboardingUiTest.kt`;
- `AdultGateUiTest.kt`;
- `CalculatorUiTest.kt`;
- `WaistToHeightUiTest.kt`;
- `SettingsUiTest.kt`;
- `HistoryUiTest.kt`;
- `HealthMetricDataStoreTest.kt`;
- new `AboutNavigationUiTest.kt`;
- new `ReleaseScreenshotCaptureTest.kt`.

Shared tests inspected:

- `BmiCalculatorTest.kt`;
- `CalculatorPropertyTest.kt`;
- `UnitConverterTest.kt`;
- `ValidationTest.kt`;
- `WaistToHeightTest.kt`.

Android/build resources/configuration inspected:

- root `build.gradle.kts`;
- `settings.gradle.kts`;
- `gradle.properties`;
- `gradle/libs.versions.toml`;
- `androidApp/build.gradle.kts`;
- `androidApp/proguard-rules.pro`;
- `AndroidManifest.xml`;
- Android values/drawable/adaptive-icon resource directories;
- `strings.xml`;
- `.gitignore`;
- `.gitattributes`;
- `.editorconfig`;
- `.env.example`.

GitHub automation/community files inspected:

- funding configuration;
- issue forms/config;
- PR template;
- release template;
- Dependabot;
- main CI;
- Android instrumentation;
- Apple shared core;
- CodeQL;
- dependency review;
- secret scan;
- tagged release workflow.

Repository policy/documentation files inspected:

- `README.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `CONTRIBUTING.md`;
- `SUPPORT.md`;
- `CODE_OF_CONDUCT.md`;
- architecture;
- backup format;
- setup;
- development;
- testing;
- release;
- troubleshooting;
- accessibility;
- performance;
- design system;
- evidence;
- GitHub governance;
- screenshot guide;
- ADR 0001;
- ADR 0002;
- ADR 0003;
- ADR 0004.

Files that were already accurate were intentionally left unchanged rather than creating meaningless churn solely to inflate commit count.

## Commit strategy

The user requested maximum commits. This continuation therefore uses many small Conventional Commits, but only when each commit represents a meaningful isolated change.

Commit categories used include:

- `chore:` accidental repository cleanup;
- `fix:` chronology, navigation, and identifier correctness;
- `test:` chronology, navigation, test isolation, screenshot evidence;
- `ci:` screenshot artifact and AAB pipeline/invariant changes;
- `build:` Unix/Windows AAB verification;
- `release:` tagged AAB packaging;
- `docs:` individual documentation/policy/template alignment.

Empty commits and artificial no-op churn are not used.

## Current automated verification state

PR #13 is open and GitHub currently reports it as mergeable.

Every content commit intentionally creates a new pull-request head and therefore supersedes older queued runs through workflow concurrency. Cancelled superseded runs should not be interpreted as product failures.

The exact final-head workflow group must include:

- CI;
- Android instrumentation;
- Apple shared core;
- CodeQL;
- Dependency Review;
- Secret Scan.

The Android instrumentation run must also publish the `android-release-screenshots` artifact containing all eight required PNG files.

At the time this handoff commit is being written, the final exact-head check group has **not yet been declared complete**. Do not merge PR #13 or mark the corresponding ROADMAP release-verification items complete until the checks for the exact final head are inspected.

## Remaining release-only/manual blockers

These are intentionally not fabricated or marked complete:

1. Physical Android hardware release-candidate review.
2. Manual TalkBack walkthrough.
3. Manual maximum-font/display-size review.
4. Manual light/dark/dynamic-color visual review beyond automated test assertions.
5. Keyboard/DPAD review where applicable.
6. Human privacy/visual approval of CI-generated release screenshots before permanent publication.
7. Protected Android production signing / Play App Signing configuration outside source control.
8. Final version/release-note update on the exact release commit.
9. `v0.1.0` tag only after every blocker above and the exact release check group pass.

The repository deliberately does not add signing keys/passwords or pretend that software automation completed human physical-device/accessibility review.

## Next exact operations for this continuation

1. Freeze product/source/document changes after this `what_changed.md` update unless CI reveals a real defect.
2. Read PR #13's exact head SHA.
3. Inspect all six workflow runs for that exact SHA.
4. Inspect failed job logs if any check fails.
5. Fix only actual root causes, with a regression/verification change where practical.
6. Confirm `android-release-screenshots` exists and contains the expected release-evidence set when Android instrumentation succeeds.
7. Update this handoff with exact final verification evidence.
8. Re-run the final exact-head workflow group after that handoff-only status commit.
9. Merge PR #13 with a normal merge commit when the final head is green.
10. Create a documentation-only post-merge handoff update if necessary so `main` does not retain stale text claiming PR #13 is still open.
11. Leave physical-device, manual accessibility, protected signing, and release tagging for the release-candidate session where those actions can be performed truthfully.
