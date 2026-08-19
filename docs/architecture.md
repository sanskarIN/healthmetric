# Architecture

## Goals

HealthMetric uses a small modular-monolith architecture that keeps health measurement rules deterministic, testable, and independent from platform UI/storage APIs.

Primary goals:

- domain logic portable across Android, JVM/Desktop, and Apple targets;
- no network requirement for core calculations;
- no raw measurement persistence unless a platform feature explicitly requires it;
- bounded and user-controlled Android local data retention;
- portable Android backups separated from device-local consent/safety state;
- fail-closed restore behavior for structurally damaged backups;
- ephemeral desktop measurement state unless a future requirement justifies persistence;
- predictable state flow and clear ownership boundaries;
- release tooling that fails closed on ambiguous artifacts/tags;
- documentation coverage for every tracked repository file;
- simple architecture that can be understood from a clean checkout.

For canonical documentation ownership and the complete file inventory, see [`documentation-map.md`](documentation-map.md) and [`repository-file-reference.md`](repository-file-reference.md).

## Dependency direction

```text
shared domain
   ↑       ↑
Android   Desktop

repository/release tooling ── verifies source/config/docs/artifacts
GitHub Actions              ── orchestrates platform-specific verification
```

`shared` must never import either client. Android and desktop may depend on `shared`. Repository tooling validates the shape and contracts around all modules but is not a runtime dependency.

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

Production files intentionally remain narrow:

- `Bmi.kt` — BMI/reference/evidence model and calculators;
- `Units.kt` — shared units/input models/conversion helpers;
- `Validation.kt` — finite/range validation and unit-specific error contracts;
- `WaistToHeight.kt` — waist-to-height result/calculation behavior.

### `androidApp`

Android application module.

Responsibilities:

- Jetpack Compose presentation;
- first-run adult-use gate and correction path;
- locale-aware numeric input/output presentation;
- local DataStore preferences/history;
- bounded JSON backup serialization and restore;
- Android Storage Access Framework document export/import;
- explicit Android share/open-link integrations;
- application ViewModel and lifecycle state;
- defensive saved-state restoration;
- finite-safe history-chart normalization;
- accessibility semantics and Android UI tests.

### `desktopApp`

Compose Multiplatform JVM desktop application module.

Responsibilities:

- desktop window/application lifecycle;
- explicit adult-use gate;
- separate under-18 unavailable path;
- metric and imperial calculator forms;
- strict dot/comma desktop numeric parsing;
- whole-feet + remaining-inches component validation for imperial height entry;
- presentation adapter around shared calculators;
- session-only light/dark theme state;
- explicit external evidence/project/funding link actions;
- desktop unit/integration tests.

The desktop client intentionally has no persistence layer. Inputs, results, adult-use choice, navigation state, and theme selection are held in memory only and disappear when the process exits.

See [`desktop.md`](desktop.md) and [`adr/0005-ephemeral-desktop-client.md`](adr/0005-ephemeral-desktop-client.md).

## Android data flow

```text
localized Compose text input
    ↓ LocalizedNumbers / field-shape parsing
shared calculator + shared validation
    ↓ deterministic numeric result
localized Compose result card
    ↓ optional record callback only when history consent is enabled
HealthMetricViewModel
    ↓
HealthMetricDataStore
    ↓
Preferences DataStore (local device only)
```

Raw weight/height/waist inputs are used transiently in UI state and are not persisted to Android calculation history. History stores only calculator type, calculated value, timestamp, identifier, and neutral summary.

## Desktop data flow

```text
Compose Desktop text input
    ↓ DesktopNumbers syntax parsing
DesktopCalculations component-shape checks
    ↓
shared calculator / validator
    ↓ deterministic numeric result or validation error
Compose Desktop result/error card
    ↓
in-memory UI state only
```

The desktop adapter does not own thresholds, formulas, or adult reference bands. Those remain in `shared`.

For split imperial height, desktop owns only the **input shape**: feet must be whole and remaining inches must be in `[0, 12)`. Once that shape is valid, the shared domain owns total adult height-range validation.

## Android state management

`HealthMetricViewModel` combines the preferences and history flows into a single immutable `HealthMetricUiState`. Compose observes this state. Mutation requests are represented as focused ViewModel methods rather than exposing DataStore directly to screens.

Form text and transient calculation results remain screen-local because they do not need application-wide ownership.

Saved navigation/filter enum names are decoded through `savedEnumValueOrDefault`. A future rename/removal therefore falls back safely instead of allowing stale `rememberSaveable` state to crash composition.

History entry deletion is persisted before the UI presents its undo snackbar. The undo action restores the sanitized entry through the ViewModel/DataStore boundary; it does not silently re-enable history saving and chronological order is recalculated by timestamp.

An accidental under-18 choice can call `resetAdultUseChoice()` to return to age selection. That reset clears only the onboarding/adult-use choice and preserves unrelated theme, retention, history consent, and saved history.

## Android chart boundary

History JSON accepts finite stored result values after sanitation. Extreme finite values can still overflow naive `max - min` arithmetic.

`ChartScale` therefore normalizes values with finite-safe scale-first arithmetic before Canvas coordinates are produced. Chart safety is intentionally a presentation concern; it does not silently rewrite imported history values or the backup schema.

## Desktop state management

Desktop Compose uses `remember` state for:

- adult-use selection;
- selected section;
- unit system;
- input text;
- current result/error;
- theme selection.

No repository, file, preferences, database, backup or synchronization abstraction is created for desktop state. This is a deliberate privacy/complexity choice recorded in ADR 0005.

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
- accepts ordinary dot or comma decimal input;
- rejects malformed/multiple-separator input;
- rejects signed/scientific/non-finite measurement syntax;
- parses whole-number feet separately for imperial forms.

`DesktopCalculations` then validates split imperial remaining inches in `[0, 12)` before combining height components. This prevents a value such as `5 ft 20 in` from being silently normalized into a different representation.

This separation prevents platform input concerns from changing shared calculation arithmetic.

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

Restore is deliberately fail-closed at the container boundary:

- unsupported schema versions are rejected;
- `history` is required to be a JSON array before any DataStore edit;
- explicit `history: []` is a valid intentional empty-history backup;
- a non-empty `history` array must retain at least one valid record after sanitation or the restore is rejected before mutation;
- when at least one record is valid, malformed neighboring records can be skipped independently;
- duplicate identifiers are removed;
- blank/overlong identifiers, negative timestamps, non-finite values and unknown calculator types are rejected;
- accepted records are sorted newest-first before the selected retention cap is applied.

This distinction prevents a damaged non-empty backup from being interpreted as a deliberate request to erase current portable history/settings.

See [`backup-format.md`](backup-format.md) for the field-level contract.

## Backup IO boundary

`BackupIo` owns raw UTF-8 stream handling for Android user-selected backup documents.

Rules:

- maximum read/write payload is 1 MiB;
- input is bounded while streaming, before JSON parsing;
- output size is checked before writing;
- streams are opened only after explicit Storage Access Framework user actions;
- backup contents are never logged.

`HealthMetricDataStore.restoreFromJson()` repeats the payload-size and structure checks at the persistence boundary so callers cannot bypass protections by skipping `BackupIo`.

After a restore document is read successfully, Android Compose requires an explicit confirmation before the DataStore mutation runs. File export similarly generates the current backup payload only after the user chooses a destination URI.

Desktop does not currently import or export this schema because it has no persistence feature.

## Error strategy

The shared domain throws explicit validation failures for invalid measurement inputs.

Android presentation catches these failures and shows user-safe validation text. Persistence import/export failures are converted into user-visible messages. Backup contents or measurement values must never be printed into error logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

Desktop presentation converts text-parse/component-shape failures into field-specific messages and shared validation failures into safe UI error cards. Desktop calculation errors are not persisted.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile`. A profile includes:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source title/publisher/URL/note;
- explicit ISO source-review date.

Android and desktop both consume this shared model. The desktop About/evidence surface reads source metadata directly from the shared reference profile rather than copying source strings into a second reference model.

This keeps future evidence updates explicit and testable rather than scattering thresholds through platform UI code.

See [`evidence.md`](evidence.md) and ADR 0003.

## Repository/documentation architecture

Documentation is part of the verified repository architecture.

[`repository-file-reference.md`](repository-file-reference.md) lists every tracked file by exact path and assigns responsibility. [`documentation-map.md`](documentation-map.md) defines canonical ownership by topic and a change-to-document update matrix.

`scripts/check_repository.py` executes `git ls-files` and requires each path to occur in the exhaustive reference. Therefore a new tracked source, resource, workflow, test, configuration file, asset or document cannot remain invisible to maintainers while the repository invariant is green.

`scripts/check_markdown_links.py` independently validates local relative Markdown links.

## Build/verification boundaries

Main Linux CI verifies:

- repository invariants, including exhaustive tracked-file documentation;
- internal Markdown links;
- repository/release Python tooling tests;
- shared/Android/desktop formatting;
- shared JVM tests;
- desktop JVM tests;
- desktop runnable-JAR packaging;
- Android JVM tests;
- Android release lint;
- debug APK assembly;
- unsigned release APK assembly;
- unsigned release AAB assembly.

A dedicated Linux emulator workflow executes connected Android tests and publishes the eight-file real-app screenshot evidence artifact.

A dedicated desktop matrix workflow runs desktop formatting, tests, current-OS runnable-JAR packaging and host-native packaging on:

- Linux → JAR + DEB;
- Windows → JAR + MSI;
- macOS → JAR + DMG.

A macOS Apple workflow compiles the iOS device and simulator shared targets and reruns shared JVM tests.

Security workflows remain separate so a normal build success cannot hide dependency, CodeQL, or repository-history secret-scan failures.

## Release architecture

Tagged publication is separated into preflight, build/staging, verification and publish boundaries.

Preflight requires:

- stable `vMAJOR.MINOR.PATCH` form;
- release version agreement with Android and desktop configuration;
- tag commit equal to current `main`;
- repository/docs/tooling checks.

Build jobs have read-only repository permission and stage artifacts through `scripts/stage_release_assets.py`, which requires exactly one non-empty expected output for each artifact type.

Final publication uses `scripts/verify_release_assets.py` to require the exact eight expected binaries with no extras, then writes `SHA256SUMS.txt`. Only the final publication job receives `contents: write`.

See [`release.md`](release.md).

## Security boundaries

HealthMetric has no required backend. The main trust boundaries are platform text input, Android imported JSON, explicit external-link intents, Android local persistence, desktop process memory, and build/release infrastructure.

Controls include:

- finite/range input validation;
- platform parsing separated from domain arithmetic;
- split imperial input-shape validation on desktop;
- explicit adult-use gates before adult reference calculators;
- recoverable but explicit Android adult-use selection;
- explicit opt-in before Android history persistence;
- user-selected bounded Android history retention;
- bounded Android backup reads/writes;
- fixed/versioned Android backup schema;
- fail-closed top-level/all-invalid Android backup structure handling;
- explicit Android restore confirmation;
- non-portable Android consent/adult-gate state;
- per-record validation and duplicate-ID protection;
- finite-safe Android history chart normalization;
- defensive Android saved-enum restoration;
- no cleartext Android traffic;
- no Android Internet permission;
- no Android application backup;
- no desktop measurement persistence;
- explicit desktop external-link actions;
- no committed signing keys/secrets;
- automated dependency/security checks;
- deterministic release tag/asset validation;
- emulator instrumentation for Android persistence and primary user flows;
- desktop tests/packaging on three operating-system families;
- Apple-target compilation on macOS;
- exhaustive tracked-file documentation verification.

## Architecture decisions

See [`adr/`](adr/) for decision records:

- ADR 0001 — shared Kotlin Multiplatform domain;
- ADR 0002 — Android local privacy-first persistence;
- ADR 0003 — versioned adult reference profiles;
- ADR 0004 — bounded user-controlled Android local data/backup;
- ADR 0005 — ephemeral desktop client.

When a future change materially alters one of these durable boundaries, create a new ADR or explicitly supersede the relevant decision rather than silently rewriting history.
