# Architecture

## Goals

HealthMetric uses a modular Kotlin Multiplatform architecture that keeps health measurement rules deterministic and portable while allowing each platform to own the integrations that genuinely belong there.

Primary goals:

- one authoritative adult-safe calculation engine across Android, desktop, web, and Apple targets;
- reusable UI where platform requirements overlap without forcing Android-specific persistence into common code;
- no backend requirement for calculations;
- no raw measurement persistence in current product history;
- bounded and user-controlled Android local data retention;
- portable backups separated from device-local consent/safety state;
- clear build and verification boundaries per platform;
- architecture understandable from a clean checkout.

## Module graph

```text
                           ┌──────────────────────────────┐
                           │            shared            │
                           │ calculations / validation /  │
                           │ adult gate / references      │
                           └──────────────┬───────────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
             ┌────────────┐       ┌──────────────┐      Android-specific
             │  sharedUI  │       │ androidApp   │      persistence + UI
             │ Compose MPP│       │ Jetpack      │
             └──────┬─────┘       │ Compose      │
                    │             └──────────────┘
          ┌─────────┼──────────┐
          │         │          │
          ▼         ▼          ▼
   desktopApp     webApp     iosApp
   JVM host       JS/Wasm    SwiftUI host
```

`androidApp` depends directly on `shared` because it has a richer Android-native UI/persistence layer. `desktopApp`, `webApp`, and `iosApp` consume `sharedUI`, which itself consumes `shared`.

## `shared`

Kotlin Multiplatform domain module.

Targets:

- Android;
- JVM/Desktop;
- JavaScript/browser;
- Wasm/browser;
- iOS x64 simulator;
- iOS arm64 device;
- iOS arm64 simulator.

Responsibilities:

- unit systems and conversion;
- adult BMI calculation;
- adult BMI reference metadata/bands and source review date;
- adult waist-to-height calculation;
- validation/domain errors;
- `HealthMetricEngine` platform façade;
- minimum adult eligibility boundary;
- cross-platform domain tests.

The module must not depend on Android UI, Compose UI, DataStore, browser DOM APIs, UIKit, platform intents, locale-specific formatting, or analytics/logging SDKs.

### `HealthMetricEngine`

`HealthMetricEngine` is the stable primitive-input façade for thin platform clients. It centralizes:

- age eligibility (`18+`);
- metric/imperial BMI routing;
- metric/imperial waist-to-height routing;
- neutral result summaries.

The UI age gate improves experience, but the domain façade repeats the adult eligibility check so client UI mistakes do not silently remove the boundary.

## `sharedUI`

Compose Multiplatform presentation module for platforms whose current requirements overlap.

Targets:

- JVM/Desktop;
- JavaScript/browser;
- Wasm/browser;
- iOS x64 simulator;
- iOS arm64 device;
- iOS arm64 simulator.

Responsibilities:

- reusable adult-use age confirmation;
- transient calculator form state;
- neutral BMI result presentation;
- neutral waist-to-height result presentation;
- responsive bounded layout;
- iOS `UIViewController` entry point through `ComposeUIViewController`.

Non-responsibilities:

- persistence/history;
- file backup;
- Android intents;
- browser storage;
- cloud synchronization;
- analytics;
- medical diagnosis or appearance/body-target interpretation.

This separation allows desktop/web/iOS to share a real UI without forcing the full Android product data model into common code.

## `androidApp`

Primary Android application module.

Responsibilities:

- Jetpack Compose presentation;
- first-run adult-use gate;
- locale-aware numeric input/output presentation;
- local DataStore preferences/history;
- bounded JSON backup serialization and restore;
- Android Storage Access Framework document export/import;
- explicit Android share/open-link integrations;
- application ViewModel/lifecycle state;
- Android accessibility semantics and instrumentation tests.

## `desktopApp`

Thin Compose Desktop host.

Responsibilities:

- create the desktop application window;
- host `HealthMetricCrossPlatformApp`;
- configure JVM 17;
- declare DMG/MSI/DEB native packaging.

It intentionally does not duplicate calculator UI or domain logic.

## `webApp`

Thin Compose Multiplatform web host.

Targets:

- JavaScript browser executable;
- Wasm browser executable.

Responsibilities:

- create the browser `ComposeViewport`;
- host `HealthMetricCrossPlatformApp`;
- provide the HTML/CSS viewport shell;
- produce JS and Wasm production bundles.

The current web client has no HealthMetric backend API and no persisted history.

## `iosApp`

Native SwiftUI host whose Xcode project is generated from `iosApp/project.yml` with XcodeGen.

Responsibilities:

- native iOS application lifecycle;
- host the `HealthMetricUI` framework's `MainViewController`;
- configure iOS deployment/build settings;
- invoke `:sharedUI:embedAndSignAppleFrameworkForXcode` before Swift compilation.

Generated Xcode project/user state is ignored by Git. The YAML specification and Swift source are the repository source of truth.

## Android data flow

```text
localized Compose text input
    ↓ parse/validate presentation number
shared calculator / HealthMetricEngine-compatible domain
    ↓ deterministic numeric result
localized Compose result card
    ↓ optional record callback
HealthMetricViewModel
    ↓
HealthMetricDataStore
    ↓
Preferences DataStore (local device only)
```

Raw weight/height/waist inputs are used transiently in UI state and are not persisted to calculation history. History stores calculator type, calculated value, timestamp, identifier, and neutral summary.

## Desktop / web / iOS data flow

```text
sharedUI transient form
    ↓ primitive numeric input
HealthMetricEngine
    ↓ validated deterministic result
sharedUI neutral result card
```

No history/persistence layer is currently attached to these beta clients.

## Android state management

`HealthMetricViewModel` combines preferences and history flows into one immutable `HealthMetricUiState`. Compose observes this state. Mutation requests are focused ViewModel methods instead of exposing DataStore directly.

Form text and transient calculation results remain screen-local.

History entry deletion is persisted before the UI presents its undo snackbar. Undo restores the sanitized entry through the ViewModel/DataStore boundary and does not silently re-enable history saving.

## Numeric localization boundary

Shared calculations operate on numeric values and remain locale-independent.

`LocalizedNumbers` belongs to the Android presentation layer and handles:

- one decimal separator per input;
- active locale separator;
- dot/comma fallback for practical keyboard compatibility;
- invalid/non-finite input rejection;
- locale-aware result/history display without digit grouping.

The shared cross-platform beta UI currently normalizes dot/comma decimal entry into a dot before numeric parsing. Rich locale formatting can be added later at the presentation layer without changing `shared` arithmetic.

## Android persistence format

DataStore preferences currently hold:

- `history_enabled`;
- `history_retention_limit`;
- `theme_mode`;
- `adult_use_confirmed`;
- `onboarding_complete`;
- `history_json`.

Supported history limits are 50, 100, 250, and 500. Default is 100. Lowering the preference immediately truncates older records.

`history_enabled`, `adult_use_confirmed`, and `onboarding_complete` are device-local consent/safety state and are never changed by portable backup restore.

Backup schema version `1` exports:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- bounded `history`.

Restore rejects unsupported schema versions and validates records independently. Duplicate identifiers are removed; invalid identifiers, timestamps, and non-finite values are rejected; restored history is capped.

See [`backup-format.md`](backup-format.md).

## Backup IO boundary

`BackupIo` owns raw UTF-8 stream handling for user-selected Android backup documents.

Rules:

- maximum read/write payload: 1 MiB;
- input is bounded while streaming before JSON parsing;
- output size is checked before writing;
- streams open only after explicit Storage Access Framework actions;
- backup contents are never logged.

`HealthMetricDataStore.restoreFromJson()` repeats the payload-size check at the persistence boundary.

After a restore document is read, Compose requires explicit confirmation before mutation. File export generates the current payload only after the user chooses a destination URI.

## Error strategy

The shared domain throws explicit validation errors for invalid measurements. Platform UI catches failures and shows safe validation text.

Android persistence import/export failures become generic user-visible messages. Backup contents or measurement values must never be printed into logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile` containing:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- source title/publisher/URL/note;
- explicit ISO source-review date.

Evidence updates therefore remain explicit and testable rather than scattered through UI code.

## Build and verification boundaries

### Linux CI

Standard CI verifies Android/JVM formatting, shared tests, Android unit tests/lint, and Android package assembly.

Cross-platform CI verifies:

- `shared` JS/Wasm compilation;
- `sharedUI` JVM/JS/Wasm compilation;
- desktop application compilation;
- JS production web bundle;
- Wasm production web bundle;
- web artifacts.

A dedicated emulator job executes Android connected tests.

### macOS CI

Apple CI verifies:

- shared JVM tests;
- shared iOS simulator/device compilation;
- sharedUI iOS simulator/device framework linking;
- XcodeGen project generation;
- unsigned SwiftUI iOS simulator host build.

Security workflows remain separate so a normal build success cannot hide dependency, CodeQL, or repository-history secret-scan failures.

## Security and privacy boundaries

HealthMetric has no required backend. Main trust boundaries are local text input, imported Android JSON, external Android intents, Android local persistence, web hosting, generated Apple build integration, and build/release infrastructure.

Controls include:

- adult eligibility enforced in reusable domain façade;
- finite/range input validation;
- locale parsing separated from domain arithmetic;
- explicit opt-in before Android history persistence;
- bounded user-selected Android retention;
- bounded Android backup IO;
- fixed backup schema;
- explicit restore confirmation;
- non-portable consent/adult-gate state;
- per-record validation and duplicate-ID protection;
- no Android Internet permission;
- no Android application backup;
- no cleartext Android traffic;
- no analytics/advertising SDK introduced by cross-platform clients;
- no committed signing keys/secrets;
- automated dependency/security checks;
- emulator instrumentation;
- desktop/web compilation;
- Apple framework and host compilation.

## Architecture decisions

See [`adr/`](adr/) for decision records. New persistence or platform-integration changes that alter privacy/data ownership should receive an ADR before implementation.
