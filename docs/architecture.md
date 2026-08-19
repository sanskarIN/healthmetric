# Architecture

## Goals

HealthMetric uses a small modular-monolith architecture that keeps health measurement rules deterministic, testable, and independent from Android UI/storage APIs.

Primary goals:

- domain logic portable across Android, JVM/Desktop, and future Apple targets;
- no network requirement for core calculations;
- no raw measurement persistence unless a future feature explicitly requires it;
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
- JSON backup export/restore;
- Android share/open-link integrations;
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

## Persistence format

DataStore preferences currently hold:

- `history_enabled`;
- `theme_mode`;
- `adult_use_confirmed`;
- `onboarding_complete`;
- `history_json`.

Export schema version `1` contains those settings plus bounded history. Restore rejects unsupported schema versions and discards malformed history rather than crashing the app.

## Error strategy

The shared domain throws explicit `ValidationError` subclasses for invalid measurement inputs. UI catches these failures and shows user-safe validation text.

Persistence import/export failures are converted into generic user-visible messages. Backup contents or measurement values must never be printed into error logs.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile`. A profile includes:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source metadata.

This keeps future evidence updates explicit and testable rather than scattering thresholds through UI code.

## Security boundaries

HealthMetric has no required backend. The main trust boundaries are local text input, imported JSON, external intents, and build/release infrastructure.

Controls include:

- finite/range input validation;
- bounded history import;
- fixed backup schema;
- no cleartext traffic;
- no Android application backup;
- no committed signing keys/secrets;
- automated dependency/security checks.

## Architecture decisions

See [`adr/`](adr/) for decision records.
