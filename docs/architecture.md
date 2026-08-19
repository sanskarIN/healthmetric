# Architecture

## Goals

HealthMetric uses a small modular-monolith architecture that keeps health measurement rules deterministic, testable, and independent from platform UI/storage APIs.

Primary goals:

- domain logic portable across Android, JVM/Desktop, and Apple targets;
- no network requirement for core calculations;
- no raw measurement persistence unless a platform feature explicitly requires it;
- bounded and user-controlled Android local data retention;
- portable Android backups separated from device-local consent/safety state;
- ephemeral desktop measurement state unless a future requirement justifies persistence;
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

The shared module must not depend on Android UI, desktop UI, DataStore, platform intents, locale display formatting, or logging.

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
- accessibility semantics and Android UI tests.

### `desktopApp`

Compose Multiplatform JVM desktop application module.

Responsibilities:

- desktop window/application lifecycle;
- explicit adult-use gate;
- separate under-18 unavailable path;
- metric and imperial calculator forms;
- tolerant dot/comma desktop numeric parsing;
- presentation adapter around shared calculators;
- session-only light/dark theme state;
- explicit external evidence/project/funding link actions;
- desktop unit/integration tests.

The desktop client intentionally has no persistence layer. Inputs, results, adult-use choice, navigation state, and theme selection are held in memory only and disappear when the process exits.

See [`desktop.md`](desktop.md) and [`adr/0005-ephemeral-desktop-client.md`](adr/0005-ephemeral-desktop-client.md).

## Android data flow

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

Raw weight/height/waist inputs are currently used transiently in UI state and are not persisted to Android calculation history. History stores only calculator type, calculated value, timestamp, identifier, and neutral summary.

## Desktop data flow

```text
Compose Desktop text input
    ↓ DesktopNumbers parsing
DesktopCalculations presentation adapter
    ↓
shared calculator / validator
    ↓ deterministic numeric result or validation error
Compose Desktop result/error card
    ↓
in-memory UI state only
```

The desktop adapter does not own thresholds, formulas, or adult reference bands. Those remain in `shared`.

## Android state management

`HealthMetricViewModel` combines the preferences and history flows into a single immutable `HealthMetricUiState`. Compose observes this state. Mutation requests are represented as focused ViewModel methods rather than exposing DataStore directly to screens.

Form text and transient calculation results remain screen-local because they do not need application-wide ownership.

History entry deletion is persisted before the UI presents its undo snackbar. The undo action restores the sanitized entry through the ViewModel/DataStore boundary; it does not silently re-enable history saving.

## Desktop state management

Desktop Compose uses `remember` state for:

- adult-use selection;
- selected section;
- unit system;
- input text;
- current result/error;
- theme selection.

No repository, file, preferences, or database abstraction is created for desktop state. This is a deliberate privacy/complexity choice recorded in ADR 0005.

## Numeric localization boundary

Shared calculations operate only on numeric values and remain locale-independent.

Android `LocalizedNumbers` handles:

- one decimal separator per input;
- the active locale's separator;
- dot/comma fallback for practical keyboard compatibility;
- rejection of invalid/non-finite input;
- locale-aware result/history display without digit grouping.

Desktop `DesktopNumbers` provides a smaller presentation-layer boundary:

- trims whitespace;
- accepts dot or comma decimal input;
- rejects malformed/multiple-separator input;
- rejects non-finite numeric values;
- parses whole-number feet separately for imperial forms.

This prevents platform input concerns from changing shared calculation arithmetic.

## Android persistence format

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

Restore rejects unsupported schema versions and validates history records individually so one malformed record does not discard valid neighbors. Duplicate identifiers are removed, invalid identifiers/timestamps/non-finite values are rejected, and the final restored collection is always capped.

See [`backup-format.md`](backup-format.md) for the field-level contract.

## Backup IO boundary

`BackupIo` owns raw UTF-8 stream handling for Android user-selected backup documents.

Rules:

- maximum read/write payload is 1 MiB;
- input is bounded while streaming, before JSON parsing;
- output size is checked before writing;
- streams are opened only after explicit Storage Access Framework user actions;
- backup contents are never logged.

`HealthMetricDataStore.restoreFromJson()` repeats the payload-size check at the persistence boundary so callers cannot bypass the limit by skipping `BackupIo`.

After a restore document is read successfully, Android Compose requires an explicit confirmation before the DataStore mutation runs. File export similarly generates the current backup payload only after the user chooses a destination URI.

Desktop does not currently import or export this schema because it has no persistence feature.

## Error strategy

The shared domain throws explicit `ValidationError` subclasses for invalid measurement inputs.

Android presentation catches these failures and shows user-safe validation text. Persistence import/export failures are converted into generic user-visible messages. Backup contents or measurement values must never be printed into error logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

Desktop presentation converts text-parse failures into field-specific messages and shared validation failures into safe UI error cards. Desktop calculation errors are not persisted.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile`. A profile includes:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source title/publisher/URL/note;
- explicit ISO source-review date.

Android and desktop both consume this shared model. The desktop About & evidence screen reads source metadata directly from `BmiReferenceProfile.AdultGeneralReference` rather than copying source strings into a second reference model.

This keeps future evidence updates explicit and testable rather than scattering thresholds through platform UI code.

## Build/verification boundaries

Main Linux CI verifies:

- repository invariants;
- internal Markdown links;
- shared/Android/desktop formatting;
- shared JVM tests;
- desktop JVM tests;
- desktop runnable-JAR packaging;
- Android JVM tests;
- Android release lint;
- debug APK assembly;
- unsigned release APK assembly;
- unsigned release AAB assembly.

A dedicated Linux emulator workflow executes connected Android tests.

A dedicated desktop matrix workflow runs desktop formatting, tests, and current-OS runnable-JAR packaging on:

- Linux;
- Windows;
- macOS.

A macOS Apple workflow compiles the iOS device and simulator shared targets and reruns shared JVM tests.

Security workflows remain separate so a normal build success cannot hide dependency, CodeQL, or repository-history secret-scan failures.

## Security boundaries

HealthMetric has no required backend. The main trust boundaries are platform text input, Android imported JSON, explicit external-link intents, Android local persistence, desktop process memory, and build/release infrastructure.

Controls include:

- finite/range input validation;
- platform parsing separated from domain arithmetic;
- explicit adult-use gates before adult reference calculators;
- explicit opt-in before Android history persistence;
- user-selected bounded Android history retention;
- bounded Android backup reads/writes;
- fixed Android backup schema;
- explicit Android restore confirmation;
- non-portable Android consent/adult-gate state;
- per-record validation and duplicate-ID protection;
- no cleartext Android traffic;
- no Android Internet permission;
- no Android application backup;
- no desktop measurement persistence;
- explicit desktop external-link actions;
- no committed signing keys/secrets;
- automated dependency/security checks;
- emulator instrumentation for Android persistence and primary user flows;
- desktop tests/packaging on three operating-system families;
- Apple-target compilation on macOS.

## Architecture decisions

See [`adr/`](adr/) for decision records.
