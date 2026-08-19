# Architecture

## Goals

HealthMetric uses a small modular-monolith architecture that keeps health measurement rules deterministic, testable, and independent from Android UI/storage APIs.

Primary goals:

- domain logic portable across Android, JVM/Desktop, and future Apple targets;
- no network requirement for core calculations;
- no raw measurement persistence unless a future feature explicitly requires it;
- bounded and user-controlled local data retention;
- predictable state flow and clear ownership boundaries;
- simple architecture that can be understood from a clean checkout.

## Modules

### `shared`

Kotlin Multiplatform domain module.

Responsibilities:

- unit systems and conversion;
- adult BMI calculation;
- adult BMI reference metadata/bands;
- adult waist-to-height calculation;
- validation and domain error types;
- cross-platform domain tests.

The shared module must not depend on Android UI, DataStore, platform intents, or logging.

### `androidApp`

Android application module.

Responsibilities:

- Jetpack Compose presentation;
- first-run adult-use gate;
- local DataStore preferences/history;
- bounded JSON backup serialization and restore;
- Android Storage Access Framework document export/import;
- explicit Android share/open-link integrations;
- application ViewModel and lifecycle state;
- accessibility semantics and Android UI tests.

## Data flow

```text
Compose screen
    ↓ validated text parsing
shared calculator
    ↓ deterministic result
Compose result card
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

History entry deletion is persisted before the UI presents its undo snackbar. The undo action restores the sanitized entry through the ViewModel/DataStore boundary; it does not silently re-enable history saving.

## Persistence format

DataStore preferences currently hold:

- `history_enabled`;
- `history_retention_limit`;
- `theme_mode`;
- `adult_use_confirmed`;
- `onboarding_complete`;
- `history_json`.

Supported history limits are 50, 100, 250, and 500 records. The default is 100. Lowering the preference immediately truncates older records.

Export schema version `1` contains those settings plus bounded history. Restore rejects unsupported schema versions and validates history records individually so one malformed record does not discard valid neighbors. Duplicate identifiers are removed, invalid identifiers/timestamps/non-finite values are rejected, and the final restored collection is always capped.

## Backup IO boundary

`BackupIo` owns raw UTF-8 stream handling for user-selected backup documents.

Rules:

- maximum read/write payload is 1 MiB;
- input is bounded while streaming, before JSON parsing;
- output size is checked before writing;
- streams are opened only after explicit Storage Access Framework user actions;
- backup contents are never logged.

`HealthMetricDataStore.restoreFromJson()` repeats the payload-size check at the persistence boundary so callers cannot bypass the limit by skipping `BackupIo`.

## Error strategy

The shared domain throws explicit `ValidationError` subclasses for invalid measurement inputs. UI catches these failures and shows user-safe validation text.

Persistence import/export failures are converted into generic user-visible messages. Backup contents or measurement values must never be printed into error logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile`. A profile includes:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source metadata.

This keeps future evidence updates explicit and testable rather than scattering thresholds through UI code.

## Security boundaries

HealthMetric has no required backend. The main trust boundaries are local text input, imported JSON, external intents, local persistence, and build/release infrastructure.

Controls include:

- finite/range input validation;
- explicit opt-in before history persistence;
- user-selected bounded history retention;
- bounded backup reads/writes;
- fixed backup schema;
- per-record validation and duplicate-ID protection;
- no cleartext traffic;
- no Android Internet permission;
- no Android application backup;
- no committed signing keys/secrets;
- automated dependency/security checks;
- emulator instrumentation for persistence and primary user flows.

## Architecture decisions

See [`adr/`](adr/) for decision records.
