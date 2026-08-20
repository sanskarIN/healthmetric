# Development Guide

## Working agreements

HealthMetric favors small, reviewable changes, a deterministic shared domain, and thin platform boundaries.

Before editing:

1. read `what_changed.md`;
2. inspect recent commits and open issues;
3. identify the smallest module/source set that owns the behavior;
4. add or update tests/build checks with the behavior change;
5. update platform documentation when a target, command, package, or parity claim changes.

For complete platform commands, see [`cross-platform.md`](cross-platform.md).

## Module ownership

### `shared` — health domain

Path:

```text
shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/
```

The shared module targets Android, JVM/Desktop, iOS ARM64 device, iOS ARM64 simulator, JavaScript browser, and WebAssembly browser.

Put platform-neutral behavior here:

- BMI calculation;
- waist-to-height calculation;
- metric/imperial conversion;
- validation;
- adult reference profiles/evidence metadata.

Rules:

- no Android UI/DataStore imports;
- no SwiftUI/UIKit imports;
- no desktop/window APIs;
- no browser DOM dependencies;
- no persistence;
- no network dependency for core calculations;
- no mutable global state;
- explicit finite/range validation;
- version evidence/reference profiles when thresholds change.

A calculator rule must not be independently copied into a platform client.

### `androidApp` — mature Android product client

Path:

```text
androidApp/src/main/java/io/github/sanskarin/healthmetric/
```

Android owns:

- Jetpack Compose Android presentation;
- Android lifecycle/ViewModel state;
- locale-aware Android numeric parsing/formatting;
- Android DataStore history/settings;
- bounded JSON backup/restore;
- Storage Access Framework document flows;
- Android share/open-link integrations;
- Android dynamic color/theme behavior;
- Android instrumentation/UI tests.

Useful subdirectories include `data/`, `ui/`, `ui/components/`, `ui/format/`, `ui/screens/`, `ui/testing/`, and `ui/theme/`.

Do not move Android DataStore/document intents directly into shared source sets. Cross-platform persistence requires an explicit abstraction and platform implementations.

### `composeApp` — shared iOS/Desktop/Web calculator client

Common UI path:

```text
composeApp/src/commonMain/kotlin/io/github/sanskarin/healthmetric/
```

Platform entry points:

```text
composeApp/src/desktopMain/
composeApp/src/iosMain/
composeApp/src/webMain/
```

`commonMain` owns reusable presentation that works across all configured Compose Multiplatform targets:

- adult-use gate;
- calculator navigation;
- metric/imperial mode selection;
- BMI/waist-to-height forms;
- validation/error presentation;
- neutral educational results.

Keep native source sets thin:

- desktop source set creates the application window;
- iOS source set creates the UIKit controller bridge;
- web source set creates the `ComposeViewport` entry point/resources.

Do not introduce platform-specific APIs into `commonMain` unless they are hidden behind a deliberate multiplatform abstraction.

### `iosApp` — native Apple host

Path:

```text
iosApp/
```

Owns:

- SwiftUI application lifecycle;
- `UIViewControllerRepresentable` bridge;
- Xcode project/scheme;
- Info.plist/application metadata;
- Apple signing/archive configuration outside committed secrets.

The Kotlin framework is produced by `composeApp` as `HealthMetricUI`. The Xcode project uses `:composeApp:embedAndSignAppleFrameworkForXcode` for direct integration.

Do not commit private Apple signing material.

## Toolchain

Current release-line versions:

```text
Kotlin:                 2.4.10
Compose Multiplatform:  1.11.1
AGP:                    8.13.2
Gradle CI:              8.13
JDK:                    17
Android SDK:            36
```

Use `gradle/libs.versions.toml` as the source of truth for centralized versions.

## Formatting and lint

Check all Kotlin application/domain modules:

```bash
gradle :shared:ktlintCheck :composeApp:ktlintCheck :androidApp:ktlintCheck
```

Format when needed:

```bash
gradle :shared:ktlintFormat :composeApp:ktlintFormat :androidApp:ktlintFormat
```

Android release lint:

```bash
gradle :androidApp:lintRelease
```

Do not silence lint merely to make CI green; fix the root cause or document a narrowly justified suppression.

## Complete local verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Set `GRADLE_BIN` if the executable is not named `gradle`.

The scripts cover shared tests, Kotlin formatting, desktop compilation, JS/Wasm production builds, browser compatibility output, Android tests/lint/packages, and the iOS simulator framework on macOS.

## Target-specific development checks

### Shared domain

```bash
gradle :shared:desktopTest
```

### Android

```bash
gradle :androidApp:testDebugUnitTest
gradle :androidApp:lintRelease
gradle :androidApp:assembleDebug
```

Connected UI/persistence tests:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

### Desktop

```bash
gradle :composeApp:compileKotlinDesktop
gradle :composeApp:run
```

Native package for the current host:

```bash
gradle :composeApp:packageDistributionForCurrentOS
```

### Web

```bash
gradle :composeApp:jsBrowserProductionWebpack
gradle :composeApp:wasmJsBrowserProductionWebpack
gradle :composeApp:composeCompatibilityBrowserDistribution
```

Use the development run task for the target being debugged:

```bash
gradle :composeApp:wasmJsBrowserDevelopmentRun
```

or:

```bash
gradle :composeApp:jsBrowserDevelopmentRun
```

### iOS/iPadOS

On macOS:

```bash
gradle :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Verify the native host:

```bash
xcodebuild -project iosApp/HealthMetric.xcodeproj -scheme HealthMetric -sdk iphonesimulator -configuration Debug CODE_SIGNING_ALLOWED=NO build
```

Also keep the lower-level shared Apple targets compiling:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

## CI ownership

Pull requests run separate workflows so platform failures are visible rather than hidden inside one giant job:

- `ci.yml` — repository checks + Android/JVM quality/builds;
- `android-instrumentation.yml` — connected Android tests;
- `cross-platform.yml` — JS/Wasm, Windows/macOS/Linux packages, iOS app build;
- `apple-shared.yml` — lower-level Apple shared-domain compilation;
- `codeql.yml`;
- `dependency-review.yml`;
- `secret-scan.yml`.

Do not delete a platform build merely because it is inconvenient on one local machine; use the appropriate CI host.

## Cross-platform feature-parity rule

A supported platform must have a buildable/runnable application client backed by the shared validated domain and a platform CI gate.

Do not claim Android-specific features on iOS/desktop/web until equivalent implementations exist and are tested. Current Android-only product integrations include persistent history, DataStore, JSON document backup/restore, dynamic color, and Android document/share intents.

When generalizing an Android-only feature:

1. identify the platform-independent contract;
2. keep deterministic rules in `shared`;
3. create an interface/expect-actual boundary only where native behavior is genuinely required;
4. add per-platform implementation/tests;
5. update the parity table and docs only after builds pass.

## Android local-data invariants

Changes to Android history/backup behavior must preserve these invariants unless an ADR deliberately replaces them:

- history disabled on fresh/default state;
- raw weight, height, and waist inputs are not persisted in history;
- supported retention limits are 50, 100, 250, and 500;
- local history never grows beyond the selected retention limit;
- individual undo does not enable future history saving;
- backup payloads are limited to 1 MiB before parsing/writing;
- unsupported top-level backup schemas are rejected;
- malformed history entries are ignored individually;
- duplicate history IDs cannot reach the UI list;
- portable backup restore never changes `history_enabled`, `adult_use_confirmed`, or `onboarding_complete`;
- restore requires explicit confirmation;
- logs never receive backup contents or measurements.

`BackupIo` is the Android stream boundary. `HealthMetricDataStore` must independently enforce size/schema/record invariants so alternate callers cannot bypass document-flow protections.

See [`backup-format.md`](backup-format.md) and ADR 0004 before changing portable data semantics.

## Numeric/localization boundaries

Shared calculations receive numeric values and remain locale-independent.

The mature Android client owns locale-aware parsing/formatting through `LocalizedNumbers`. The current shared Compose client accepts practical dot/comma decimal input but does not duplicate Android persistence or locale plumbing.

When changing numeric input:

- keep finite/range validation in `shared`;
- test dot/comma input behavior where presentation accepts it;
- do not interpret grouping separators as measurement data;
- keep displayed precision intentional and tested.

## Health reference changes

Do not modify adult reference thresholds as a UI-only edit.

A reference change requires:

1. versioned shared reference-profile update;
2. authoritative evidence metadata;
3. updated `reviewedOnIsoDate`;
4. boundary tests;
5. neutral educational explanation;
6. evidence/changelog/release-note updates.

Do not convert reference bands into appearance scores, body rankings, or personalized appearance goals.

## Privacy review questions

For every feature, ask:

- Does it require storing new data?
- Can it work offline?
- Is the stored data necessary?
- Can the user delete/export it where persistence exists?
- Is retention bounded and understandable?
- Is state portable, or consent/safety state that must remain local?
- Could imported content consume excessive memory/CPU?
- Could logs reveal measurements or backup content?
- Does it introduce a third-party SDK/network endpoint?
- Does a new platform implementation accidentally weaken an existing privacy boundary?

Prefer less data, bounded inputs, fewer permissions, and explicit user actions.

## UI review questions

Across platforms:

- Is hierarchy readable at large text/scaling?
- Are touch/click targets usable?
- Are controls screen-reader labeled?
- Is meaning available without color alone?
- Is the adult-only/non-diagnostic wording intact?
- Does the layout work on phone, tablet, desktop window, and narrow browser sizes?
- Does keyboard/focus navigation work where applicable?
- Do test hooks supplement rather than replace accessible semantics?

## Dependencies and workflows

Prefer maintained dependencies from Kotlin/JetBrains, AndroidX, and other trusted upstream sources. Dependabot changes must pass the full affected-platform matrix before merge.

When updating GitHub Actions, keep permissions least-privilege. Pull-request code must not receive unnecessary write permissions or secrets.

## Commit strategy

Use small meaningful commits and Conventional Commit prefixes. Do not create empty commits or churn solely to inflate commit count.
