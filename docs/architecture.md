# Architecture

## Goals

HealthMetric uses a small modular-monolith architecture that keeps health measurement rules deterministic, testable, and independent from Android UI/storage APIs.

Primary goals:

- domain logic portable across Android, JVM/Desktop, and Apple targets;
- no network requirement for core calculations;
- no raw measurement persistence unless a future feature explicitly requires it;
- bounded and user-controlled local data retention;
- portable backups separated from device-local consent/safety state;
- deterministic newest-first history ordering;
- predictable state flow and clear ownership boundaries;
- simple architecture that can be understood from a clean checkout.

## Modules

### `shared`

Kotlin Multiplatform domain module.

Targets:

- Android;
- JVM/Desktop;
- iOS arm64 device;
- iOS arm64 simulator.

Responsibilities:

- unit systems and conversion;
- adult BMI calculation;
- adult BMI reference metadata/bands and source review date;
- adult waist-to-height calculation;
- validation and domain error types;
- cross-platform domain tests.

The shared module must not depend on Android UI, DataStore, platform intents, locale formatting, or logging.

### `androidApp`

Android application module.

Responsibilities:

- Jetpack Compose presentation;
- first-run adult-use gate;
- locale-aware numeric input/output presentation;
- local DataStore preferences/history;
- bounded JSON backup serialization and restore;
- Android Storage Access Framework document export/import;
- explicit Android share/open-link integrations;
- application ViewModel and lifecycle state;
- accessibility semantics and Android UI tests;
- deterministic emulator release-screenshot evidence.

## Data flow

```text
localized Compose text input
    ↓ parse/validate presentation number
shared calculator
    ↓ deterministic numeric result
localized Compose result card
    ↓ optional record callback
HealthMetricViewModel
    ↓
HealthMetricDataStore
    ↓
Preferences DataStore (local device only)
```

Raw weight/height/waist inputs are currently used transiently in UI state and are not persisted to calculation history. History stores only calculator type, calculated value, timestamp, identifier, and neutral summary.

## State management

`HealthMetricViewModel` combines the preferences and history flows into a single immutable `HealthMetricUiState`. Compose observes this state. Mutation requests are represented as focused ViewModel methods rather than exposing DataStore directly to screens.

Form text and transient calculation results remain screen-local because they do not need application-wide ownership.

New history entries receive UUID identifiers before entering the persistence layer. DataStore still validates imported/programmatic identifiers and removes duplicate IDs.

History entry deletion is persisted before the UI presents its undo snackbar. The undo action restores the sanitized entry through the ViewModel/DataStore boundary; it does not silently re-enable history saving. Restored items are normalized into timestamp-descending order rather than being forced to the top.

The app-level screen state also records the screen that opened About. Both the visible back button and Android system back return from About to that originating screen while bottom navigation remains hidden on About.

## Numeric localization boundary

Shared calculations operate only on numeric values and remain locale-independent. `LocalizedNumbers` belongs to the Android presentation layer and handles:

- one decimal separator per input;
- the active locale's separator;
- dot/comma fallback for practical keyboard compatibility;
- rejection of invalid/non-finite input;
- locale-aware result/history display without digit grouping.

This prevents locale concerns from changing shared calculation arithmetic.

## Persistence format

DataStore preferences currently hold:

- `history_enabled`;
- `history_retention_limit`;
- `theme_mode`;
- `adult_use_confirmed`;
- `onboarding_complete`;
- `history_json`.

Supported history limits are 50, 100, 250, and 500 records. The default is 100. Lowering the preference immediately truncates older records.

These DataStore values do not all have the same portability policy. `history_enabled`, `adult_use_confirmed`, and `onboarding_complete` are device-local consent/safety state and are never changed by portable backup restore.

Backup schema version `1` currently exports:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- bounded `history`.

Restore rejects unsupported schema versions and validates history records individually so one malformed record does not discard valid neighbors. Duplicate identifiers are removed, invalid identifiers/timestamps/non-finite values are rejected, accepted entries are sorted newest-first by timestamp, and only then is the retention cap applied.

See [`backup-format.md`](backup-format.md) for the field-level contract.

## Backup IO boundary

`BackupIo` owns raw UTF-8 stream handling for user-selected backup documents.

Rules:

- maximum read/write payload is 1 MiB;
- input is bounded while streaming, before JSON parsing;
- output size is checked before writing;
- streams are opened only after explicit Storage Access Framework user actions;
- backup contents are never logged.

`HealthMetricDataStore.restoreFromJson()` repeats the payload-size check at the persistence boundary so callers cannot bypass the limit by skipping `BackupIo`.

After a restore document is read successfully, Compose requires an explicit confirmation before the DataStore mutation runs. File export similarly generates the current backup payload only after the user chooses a destination URI.

## Error strategy

The shared domain throws explicit `ValidationError` subclasses for invalid measurement inputs. UI catches these failures and shows user-safe validation text.

Persistence import/export failures are converted into generic user-visible messages. Backup contents or measurement values must never be printed into error logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile`. A profile includes:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source title/publisher/URL/note;
- explicit ISO source-review date.

This keeps future evidence updates explicit and testable rather than scattering thresholds through UI code.

## UI automation and screenshot evidence

`HealthMetricTestTags` provides stable semantics hooks for navigation and critical calculator/settings controls while preserving user-facing accessibility semantics.

`ReleaseScreenshotCaptureTest` drives the real app through a deterministic release-evidence journey. It resets local state, uses fictional/example values, captures the required PNG set through Android `UiAutomation`, and writes it to app-scoped external storage. The emulator workflow pulls those files and uploads the `android-release-screenshots` artifact. This is regression/release evidence, not a substitute for human visual/accessibility review.

## Build/verification boundaries

Linux CI verifies repository/document invariants, Markdown links, Android/JVM formatting, shared tests, Android unit tests, release lint, debug APK assembly, unsigned release APK assembly, and unsigned release App Bundle assembly. Expected lint/APK/AAB artifacts are uploaded by the workflow.

A dedicated Linux API 35 emulator job executes connected Android tests and publishes the required screenshot-evidence artifact. macOS CI compiles the iOS device and simulator targets and reruns shared JVM tests.

Security workflows remain separate so a normal build success cannot hide dependency, CodeQL, or repository-history secret-scan failures.

## Security boundaries

HealthMetric has no required backend. The main trust boundaries are local text input, imported JSON, external intents, local persistence, and build/release infrastructure.

Controls include:

- finite/range input validation;
- locale parsing separated from domain arithmetic;
- explicit opt-in before history persistence;
- user-selected bounded history retention;
- deterministic newest-first history normalization;
- bounded backup reads/writes;
- fixed backup schema;
- explicit restore confirmation;
- non-portable consent/adult-gate state;
- per-record validation and duplicate-ID protection;
- collision-resistant new local history IDs;
- no cleartext traffic;
- no Android Internet permission;
- no Android application backup;
- no committed signing keys/secrets;
- automated dependency/security checks;
- repository invariant checks for required release packaging/evidence files;
- emulator instrumentation for persistence and primary user flows;
- Apple-target compilation on macOS.

## Architecture decisions

See [`adr/`](adr/) for decision records.
