# Architecture

## Goals

HealthMetric uses a small modular architecture that keeps adult health-measurement rules deterministic, testable, and independent from any single UI or storage platform.

Primary goals:

- one calculation/validation implementation across Android, iOS/iPadOS, desktop, JavaScript, and WebAssembly;
- no network requirement for core calculations;
- adult-only reference tools with neutral, non-diagnostic presentation;
- bounded and user-controlled persistence where persistence exists;
- portable backups separated from device-local consent/safety state;
- platform entry points kept thin;
- buildability enforced by platform-specific CI runners;
- clear separation between platform support and feature parity.

## Module overview

```text
androidApp ───────┐
                  ├────> shared
composeApp ───────┘
   │
   ├── desktop entry point
   ├── web entry point
   └── iOS UIViewController bridge <──── iosApp (SwiftUI/Xcode host)
```

## `shared`

Kotlin Multiplatform domain module.

Targets:

- Android;
- JVM/Desktop;
- iOS ARM64 device;
- iOS ARM64 simulator;
- JavaScript browser;
- WebAssembly browser.

Responsibilities:

- metric and imperial unit systems/conversion;
- adult BMI calculation;
- versioned adult BMI reference metadata and evidence review date;
- adult waist-to-height calculation;
- finite-value and plausible-range validation;
- domain error types;
- deterministic cross-platform tests.

The shared module must not depend on Android UI, Android DataStore, platform document pickers, platform share intents, or presentation formatting.

## `androidApp`

The mature Android application module.

Responsibilities:

- Jetpack Compose Android presentation;
- first-run adult-use gate;
- locale-aware Android numeric input/output;
- local DataStore preferences/history;
- bounded JSON backup serialization and restore;
- Android Storage Access Framework document import/export;
- explicit Android share/open-link integrations;
- Android ViewModel/lifecycle state;
- Android dynamic color and branded launch behavior;
- accessibility semantics and Android UI tests.

Android remains the most feature-complete client because its persistence/backup integrations predate the cross-platform UI work.

## `composeApp`

Compose Multiplatform application module shared by iOS/iPadOS, Windows, macOS, Linux, JavaScript web, and WebAssembly web.

Targets:

- JVM desktop;
- iOS ARM64 device;
- iOS ARM64 simulator;
- JavaScript browser;
- WebAssembly browser.

Responsibilities:

- reusable adult-use confirmation;
- metric/imperial selection;
- adult BMI form/result presentation;
- waist-to-height form/result presentation;
- invocation of `shared` calculators and validation;
- neutral/non-diagnostic result copy;
- common cross-platform Material presentation;
- desktop native-distribution configuration;
- Apple framework production;
- browser executable production.

Platform entry points are intentionally small:

- `desktopMain` creates the application window;
- `webMain` creates the browser `ComposeViewport`;
- `iosMain` creates the UIKit view controller consumed by SwiftUI.

## `iosApp`

Native Apple host application.

Responsibilities:

- SwiftUI application lifecycle;
- Xcode project/scheme;
- iPhone/iPad application metadata;
- bridging the `HealthMetricUI` Kotlin framework through `UIViewControllerRepresentable`;
- Apple simulator/device/archive build configuration.

The Xcode project uses Kotlin direct framework integration through `:composeApp:embedAndSignAppleFrameworkForXcode`.

Production Apple signing remains external to source control.

## Desktop architecture

Windows, macOS, and Linux share the same `desktopMain` entry point and common Compose UI.

The desktop client is a JVM application packaged through Compose Desktop native distributions:

- Windows → MSI;
- macOS → DMG;
- Linux → DEB.

Native package generation is host-specific, so CI runs a matrix on all three operating systems.

## Web architecture

The browser application has two Kotlin targets:

- `wasmJs` for the modern WebAssembly path;
- `js` for JavaScript compatibility/fallback.

Both targets use the `webMain` entry point and the same `App()` composable. The web resource directory provides an explicit HTML shell and viewport CSS.

Browser calculation is client-side; the health domain does not require a backend API.

## Cross-platform calculator data flow

```text
Compose text input
    ↓ parse presentation value
shared validation + calculator
    ↓ deterministic numeric result
shared Compose result card
```

The cross-platform calculator client currently keeps form/result state in the local UI session and does not silently persist raw measurements.

## Android data flow

```text
localized Android Compose text input
    ↓ parse/validate presentation number
shared calculator
    ↓ deterministic numeric result
localized Android result card
    ↓ optional record callback
HealthMetricViewModel
    ↓
HealthMetricDataStore
    ↓
Preferences DataStore (local Android device only)
```

Raw weight/height/waist inputs are transient and are not written to Android history. Android history stores only calculator type, calculated value, timestamp, identifier, and a neutral summary.

## State management

### Shared Compose client

Form input, calculator selection, measurement system, adult-use confirmation for the current session, errors, and results are held as Compose state. No cross-platform storage implementation is inferred from Android DataStore.

### Android client

`HealthMetricViewModel` combines preference/history flows into an immutable `HealthMetricUiState`. Compose observes that state. Mutation requests are focused ViewModel operations rather than direct DataStore access from screens.

History entry deletion persists before the UI presents undo. Undo restores the sanitized record without silently enabling history saving.

## Numeric localization boundary

Domain arithmetic remains locale-independent.

Android's `LocalizedNumbers` handles the mature Android client's locale-aware parsing/formatting. The shared Compose client accepts practical dot/comma decimal input and passes numeric values into the same domain validation.

Future localization improvements belong in presentation/platform utilities; they must not change calculator formulas.

## Android persistence format

Android DataStore preferences hold:

- `history_enabled`;
- `history_retention_limit`;
- `theme_mode`;
- `adult_use_confirmed`;
- `onboarding_complete`;
- `history_json`.

Supported history limits are 50, 100, 250, and 500 records, with 100 as the default. Lowering the setting immediately truncates older records.

`history_enabled`, `adult_use_confirmed`, and `onboarding_complete` are device-local consent/safety state and are never overwritten by portable backup restore.

Backup schema version 1 exports:

- `schemaVersion`;
- `historyRetentionLimit`;
- `themeMode`;
- bounded `history`.

See [`backup-format.md`](backup-format.md).

## Android backup IO boundary

`BackupIo` owns bounded UTF-8 stream handling for user-selected Android backup documents.

Rules:

- maximum read/write payload: 1 MiB;
- input bounded before JSON parsing;
- output checked before writing;
- streams opened only after explicit user actions;
- backup contents never logged;
- restore requires explicit confirmation before mutation.

`HealthMetricDataStore.restoreFromJson()` independently repeats the payload-size check so non-UI callers cannot bypass it.

## Error strategy

The shared domain uses explicit validation errors for invalid measurement inputs. Platform UI converts failures into user-safe validation messages.

Android persistence import/export failures are surfaced generically. Backup contents and measurement values must not be printed to operational logs. `SafeLogger` accepts fixed event identifiers and sanitized exception type names only.

## Evidence/reference model

Adult BMI thresholds are grouped inside a versioned `BmiReferenceProfile` containing:

- stable identifier;
- display name;
- adult-only flag;
- ordered reference bands;
- evidence source metadata;
- explicit ISO source-review date.

This keeps future evidence changes explicit and testable rather than scattering thresholds through platform UI code.

## Build and verification boundaries

### Standard Android CI

Linux CI verifies repository invariants, Markdown links, formatting, shared JVM tests, Android JVM tests, Android lint, and Android package assembly.

### Android instrumentation

A dedicated emulator workflow executes Android connected tests.

### Cross-platform CI

The cross-platform workflow verifies:

- JS production build;
- Wasm production build;
- browser compatibility distribution;
- Linux desktop compile/package;
- Windows desktop compile/package;
- macOS desktop compile/package;
- Kotlin iOS simulator framework;
- native Xcode iOS simulator application.

### Apple shared-core CI

The existing Apple workflow continues to compile shared domain Apple targets independently of the application-level iOS check.

### Security workflows

CodeQL, dependency review, and repository-history secret scanning remain separate gates so a successful application build cannot hide a security failure.

## Security and privacy boundaries

HealthMetric has no required backend. Main trust boundaries are local text input, Android imported JSON, external platform intents, local persistence, browser/static assets, native packaging, and release infrastructure.

Controls include:

- finite/range domain validation;
- adult-use confirmation before adult reference calculators;
- neutral non-diagnostic copy;
- explicit Android opt-in before history persistence;
- bounded Android retention and backup IO;
- fixed Android backup schema;
- non-portable consent/adult-gate state;
- per-record import validation and duplicate-ID protection;
- no Android cleartext traffic;
- no Android Internet permission for the offline core;
- no Android application backup;
- no committed distribution signing keys/secrets;
- automated dependency/security checks;
- emulator instrumentation;
- native desktop packaging on real host runners;
- iOS framework and Xcode simulator builds on macOS.

## Feature parity rule

A platform may be described as supported when it has a buildable/runnable application client backed by the shared validated calculator domain and is covered by platform CI.

Do not describe platform-specific Android persistence, backup, dynamic color, or document integration as available on iOS/desktop/web until equivalent implementations actually exist and are tested.

See [`cross-platform.md`](cross-platform.md) for the current parity matrix.

## Architecture decisions

See [`adr/`](adr/) for decision records.
