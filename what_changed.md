# HealthMetric — Work Handoff

Last updated: 2026-08-19

## Current milestone

Active development branch:

`phase2/data-controls-and-regression-tests`

Open pull request:

- PR #12 — `feat: complete privacy data controls and release verification`
- https://github.com/sanskarIN/healthmetric/pull/12

Base branch: `main`

Base commit for this continuation: `8c8d2cda0093acbbadfc55d3e03e14dbe4f406ea`

Branch head immediately before this handoff update: `75f076431f5cbb50d040a0727685823b0075ca91` (`docs: align contribution checks with full quality gates`).

PR #12 contained 125 commits at that head. This handoff update adds another meaningful documentation commit.

Current project stage:

- Phase 0 repository foundation: implemented.
- Phase 1 clean Android end-to-end MVP: implemented.
- Phase 2 product completeness: implemented except real device/emulator screenshot capture.
- Phase 3 advanced shared-core quality: iOS targets, Apple CI, evidence review metadata, and locale-aware Android numeric presentation implemented; optional future desktop UI remains deliberately uncommitted because it is not required for the Android-first product.
- Phase 4 verification depth: automated coverage substantially expanded, including Android emulator instrumentation and backup/persistence regression coverage; manual TalkBack/accessibility evidence remains a release-candidate task.
- Phase 5 release readiness: automated debug/release assembly workflows exist; protected production signing, physical-device review, screenshots, and final successful release-candidate checks remain external/release tasks.
- Phase 6 final audit: repository/document integrity checks are automated; final release audit cannot be marked complete until the release-candidate checks finish successfully.

## Continuation objective completed in this branch

The continuation focused on completing the remaining privacy/data-control work, eliminating restore/consent safety gaps, strengthening automated regression coverage, improving platform readiness, and turning repository requirements into executable CI invariants.

## Product/data changes

### Privacy-first history retention

Added `HistoryRetentionPolicy`:

- default retention: 100 results;
- supported user-selectable limits: 50, 100, 250, 500;
- hard maximum: 500;
- unsupported values normalize to 100.

`AppPreferences` now stores the selected retention limit.

`HealthMetricDataStore` now:

- persists the retention setting;
- trims existing history immediately when the user lowers the limit;
- bounds new history to the selected limit;
- caps imported history;
- sanitizes records before storage;
- prevents duplicate history IDs from reaching the UI.

### Individual history deletion and undo

Added:

- per-entry history delete action;
- accessible delete icon description;
- persisted deletion before feedback is presented;
- snackbar `Undo` action;
- restore of the deleted sanitized record;
- restore behavior that does not enable future history saving.

Erase-all history continues to require explicit confirmation.

### Explicit file backup

Settings now separates:

- `Save JSON backup to a file`;
- `Share JSON backup`;
- `Restore from JSON backup`.

File backup uses Android's Storage Access Framework `CreateDocument` contract. JSON is generated after the user has selected a destination URI rather than being retained in transient state while the picker is open.

Share backup continues to use an explicit Android chooser.

### Defensive backup IO

Added `BackupIo` with a 1 MiB UTF-8 payload limit.

Protections:

- input is counted while streaming before JSON parsing;
- output size is checked before writing;
- `HealthMetricDataStore.restoreFromJson()` independently repeats the UTF-8 byte-size check so non-UI callers cannot bypass it;
- unsupported schema versions fail before the DataStore mutation;
- malformed individual history records are skipped rather than invalidating valid neighboring records;
- blank IDs are rejected;
- IDs are bounded to 96 characters;
- summaries are bounded to 240 characters;
- negative timestamps are rejected;
- non-finite values are rejected;
- unknown calculator types are rejected;
- duplicate IDs are deduplicated;
- restored history is always bounded.

### Restore confirmation

Selecting a backup file no longer immediately replaces portable local data.

Flow:

1. user explicitly selects a local document;
2. HealthMetric reads it through bounded `BackupIo`;
3. the app shows a restore confirmation dialog;
4. only the explicit `Restore` action invokes the DataStore restore transaction;
5. cancel leaves data unchanged.

### Device-local consent and adult safety state

A critical privacy/safety boundary was tightened.

Portable backups no longer export and restore cannot change:

- `history_enabled` / prior JSON `historyEnabled`;
- `adult_use_confirmed` / prior JSON `adultUseConfirmed`;
- `onboarding_complete` / prior JSON `onboardingComplete`.

This means importing another person's backup cannot:

- silently enable future local history collection;
- enable adult-only BMI/waist reference screens;
- bypass the current installation's onboarding/adult-use state.

Legacy schema-v1 documents containing those fields remain readable, but those fields are deliberately ignored.

Portable schema-v1 fields are now:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- bounded `history`.

The exact contract is documented in `docs/backup-format.md` and ADR 0004.

## Numeric localization changes

Added `LocalizedNumbers` in the Android presentation layer.

It provides:

- decimal input validation;
- one decimal separator maximum;
- active locale decimal separator support;
- practical dot/comma fallback;
- finite numeric parsing;
- locale-aware number display without grouping.

BMI Android UI now:

- parses metric/imperial decimal fields through `LocalizedNumbers`;
- displays BMI using locale-aware one-decimal formatting.

Waist-to-height UI now:

- parses decimal fields through `LocalizedNumbers`;
- displays ratios using locale-aware two-decimal formatting.

History cards and chart accessibility summaries use the same locale-aware formatting rules.

Shared domain arithmetic remains locale-independent.

## Shared-platform changes

The Kotlin Multiplatform `shared` module now explicitly configures:

- Android target;
- JVM/Desktop target;
- `iosArm64`;
- `iosSimulatorArm64`.

Added `.github/workflows/apple-shared.yml` on `macos-latest` to:

- run shared JVM tests;
- compile the iOS simulator target;
- compile the iOS device target.

No iOS UI is claimed or included; the change validates the shared health calculation core for future Apple clients.

## Evidence/reference changes

`EvidenceSource` now contains an explicit `reviewedOnIsoDate` field.

The existing WHO adult BMI source was reviewed on 2026-08-19 and the embedded source metadata now records that date.

`docs/evidence.md` now documents:

- source title/publisher/URL;
- adult-only scope;
- current reference boundaries;
- review date;
- required update workflow for future evidence changes.

Shared tests verify the source metadata and review date.

## Android branding changes

Added modern launcher resources:

- adaptive launcher icon;
- round adaptive icon;
- Android 13+ monochrome/themed adaptive icon variants;
- shared launcher background resource;
- dedicated adaptive icon foreground vector.

`AndroidManifest.xml` now uses the adaptive/round launcher resources.

Existing branded startup splash treatment remains in place.

Security manifest invariants remain:

- no `INTERNET` permission;
- `android:allowBackup="false"`;
- `android:usesCleartextTraffic="false"`.

## UI automation support

Added stable `HealthMetricTestTags` for critical Compose controls.

Tagged UI includes:

- BMI weight field;
- BMI metric height field;
- BMI imperial feet/inches fields;
- BMI calculate action/result;
- waist measurement field;
- waist height field;
- waist calculate action/result;
- history list;
- settings history opt-in switch.

Tags supplement accessible semantics; they do not replace user-facing labels.

## Tests added/expanded

### Android JVM tests

`AppPreferencesTest` now verifies:

- history disabled by default;
- default retention value;
- supported retention values normalize to themselves;
- unsupported retention values fall back to default.

`BackupIoTest` verifies:

- UTF-8 round trip;
- oversized input rejection;
- oversized output rejection.

`LocalizedNumbersTest` verifies:

- dot decimal input;
- comma decimal input;
- multiple-separator rejection;
- invalid unit/text rejection;
- US and German representative locale parsing;
- fallback separator parsing;
- non-finite/invalid parsing rejection;
- locale-aware display formatting.

### Android instrumentation tests

Added/expanded:

`CalculatorUiTest`

- metric BMI success journey;
- result rendering;
- missing-weight validation.

`WaistToHeightUiTest`

- metric ratio success journey;
- result precision;
- missing-waist validation.

`AdultGateUiTest`

- under-18 onboarding choice dispatch;
- adult-reference-unavailable screen explanation.

`SettingsUiTest`

- explicit history opt-in callback;
- retention selection callback;
- file-backup action callback;
- share-backup action callback.

`HistoryUiTest`

- per-entry delete callback;
- erase-all confirmation dialog;
- destructive confirmation callback.

`HealthMetricDataStoreTest`

- history is not stored without opt-in;
- selected retention trims older entries;
- portable export/restore round trip;
- unsupported schema rejection;
- entry delete/restore behavior;
- undo restore does not change disabled history preference;
- malformed-record recovery;
- duplicate-ID handling;
- invalid programmatic history rejection;
- portable backup omission of history/adult/onboarding state;
- legacy backup fields cannot overwrite current device consent/adult gate.

Existing shared calculation, validation, conversion, boundary, and deterministic property tests remain.

## GitHub Actions/automation changes

### Main CI

`.github/workflows/ci.yml` now runs:

1. repository invariant audit;
2. internal Markdown link audit;
3. JDK 17 setup;
4. Gradle 8.13 setup;
5. Android SDK 36 / Build Tools 35.0.0 setup;
6. shared + Android ktlint checks;
7. shared JVM tests;
8. Android JVM tests;
9. Android release lint;
10. debug APK assembly;
11. unsigned release APK assembly;
12. lint/APK artifact upload.

### Android instrumentation

New `.github/workflows/android-instrumentation.yml`:

- API 35 Google APIs x86_64 emulator;
- KVM enablement;
- animation disablement;
- `gradle :androidApp:connectedDebugAndroidTest`;
- instrumentation report artifact upload.

### Apple shared core

New `.github/workflows/apple-shared.yml`:

- `macos-latest`;
- shared JVM tests;
- iOS simulator compilation;
- iOS device compilation.

### Security/release workflow maintenance

Workflow dependencies were modernized to their current configured major versions where applicable:

- `actions/checkout@v7`;
- `actions/setup-java@v5`;
- `gradle/actions/setup-gradle@v6`;
- `actions/upload-artifact@v7`;
- `github/codeql-action@v4`;
- `actions/dependency-review-action@v5`.

Secret scan continues to use `gitleaks/gitleaks-action@v2` with full Git history checkout.

Release workflow now also executes Android unit tests and release lint before creating the unsigned release artifact.

## Repository integrity automation

Added `scripts/check_repository.py`.

It verifies:

- required repository/documentation/workflow paths exist;
- Android manifest does not request Internet permission;
- Android app backup stays disabled;
- cleartext traffic stays disabled;
- README retains required credit, funding, contacts, and MIT metadata;
- privacy documentation retains key default/adult/backup invariants.

Added `scripts/check_markdown_links.py`.

It verifies internal relative Markdown targets without making network requests.

Both scripts run before Gradle work in CI.

## Reproducible developer verification

Added:

- `scripts/verify.sh`;
- `scripts/verify.ps1`.

They run the complete non-device verification sequence:

- shared and Android ktlint;
- shared JVM tests;
- Android JVM tests;
- Android release lint;
- Android debug assembly;
- Android unsigned release assembly.

`GRADLE_BIN` can override the executable name/path.

## Documentation added/updated

Added:

- `docs/backup-format.md`;
- ADR 0004: bounded, user-controlled local data.

Updated:

- `README.md`;
- `PRIVACY.md`;
- `SECURITY.md`;
- `CONTRIBUTING.md`;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `docs/architecture.md`;
- `docs/setup.md`;
- `docs/development.md`;
- `docs/testing.md`;
- `docs/release.md`;
- `docs/troubleshooting.md`;
- `docs/accessibility.md`;
- `docs/performance.md`;
- `docs/evidence.md`;
- `.github/PULL_REQUEST_TEMPLATE.md`;
- this `what_changed.md`.

Documentation now matches:

- opt-in history default;
- retention behavior;
- file/share backup separation;
- 1 MiB backup boundary;
- restore confirmation;
- non-portable consent/adult-gate state;
- locale-aware numeric behavior;
- iOS shared targets and macOS CI;
- current automated verification gates;
- adaptive launcher branding.

## Commands/tool checks performed during this continuation

Repository state was inspected through the authenticated GitHub connector before editing.

GitHub workflow/action configuration was checked against the current upstream action repositories before the workflow major-version changes.

The WHO adult BMI evidence source was re-reviewed before adding `reviewedOnIsoDate` to source metadata.

A direct public `git clone` was attempted inside the execution container to run the new Python repository checks locally, but the container cannot resolve external network hosts. This is an execution-environment network limitation, not a repository failure.

The original execution environment still does not provide an Android SDK/Gradle installation suitable for authoritative local Android build verification. Therefore Android/JVM/Apple build results must come from the configured GitHub Actions runners.

## Pull-request verification state at this handoff update

PR #12 is open and GitHub reports it as mergeable with a clean merge state.

The final check group before this handoff update was queued for branch head `75f076431f5cbb50d040a0727685823b0075ca91`:

- CI run `32214845875`;
- Android instrumentation run `32214845908`;
- Apple shared core run `32214845780`;
- Dependency Review run `32214846130`;
- CodeQL run `32214845896`;
- Secret Scan run `32214845883`.

Earlier runs were repeatedly cancelled/superseded by the intentionally granular sequence of branch commits. Do not interpret those superseded runs as product test failures.

Because this `what_changed.md` update changes the branch head, GitHub will schedule one final check group for this new documentation commit. Inspect that final group before merging.

## Commit identity

Requested commit email:

`sanskarin@outlook.in`

GitHub Actions run metadata for this branch shows the generated commits with:

- author name: `Sanskar`;
- author email: `sanskarin@outlook.in`;
- committer email: `sanskarin@outlook.in`.

## Known limitations / external release blockers

These are intentionally not claimed complete:

1. Real screenshots cannot be captured in the current coding environment because it does not expose an interactive Android device/emulator display.
2. Manual TalkBack, physical-device, keyboard/DPAD, and large-font accessibility evidence still requires a device/emulator interaction session.
3. Production Android signing keys/passwords are intentionally not stored in GitHub source. A protected signing/distribution environment must be configured before a store release.
4. The repository's automated checks must complete successfully on the final branch/release commit before `v0.1.0` is tagged.
5. No desktop or iOS UI client is currently shipped; only the tested/configured shared core is cross-platform. Android remains the primary product as specified.

These limitations are release/environment tasks rather than TODO placeholders in core product code.

## Next exact tasks

1. Allow the GitHub Actions group for the final `what_changed.md` branch head to run.
2. Inspect every workflow/job result and logs.
3. If any build/test/lint/security/documentation check fails, fix the root cause with a regression/verification change and rerun the failed checks.
4. When PR #12 is fully green, merge it into `main` using a merge commit so the granular development history is preserved.
5. Confirm `main` receives the merged implementation.
6. Before `v0.1.0`, perform the remaining physical/manual release checks: screenshots, TalkBack/large-font/device review, protected production signing, and final release-candidate verification.
7. Do not tag `v0.1.0` until all release blockers are resolved.

## Release notes draft

HealthMetric's next development release adds bounded opt-in local history, configurable retention, individual delete/undo, explicit file/share backup flows, defensive bounded restore parsing, non-portable privacy/adult-gate state, restore confirmation, locale-aware numeric presentation, iOS shared-core targets, expanded Android instrumentation, Apple compilation CI, adaptive launcher branding, evidence review metadata, repository integrity checks, and significantly expanded privacy/security/testing documentation.
