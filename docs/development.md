# Development Guide

## Working agreements

HealthMetric favors small, reviewable changes and a deterministic shared domain layer.

Before editing:

1. read `what_changed.md` for the active continuation state;
2. inspect recent commits and open pull requests/issues;
3. read [`documentation-map.md`](documentation-map.md) to identify the canonical contract documents affected by the change;
4. use [`repository-file-reference.md`](repository-file-reference.md) to identify the exact responsibility of files you plan to touch;
5. identify the smallest module that owns the behavior;
6. add or update tests with the behavior change;
7. preserve adult-only, privacy-first, neutral educational boundaries.

## Module ownership

### Shared domain

Put platform-neutral calculation behavior under:

`shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/`

Rules:

- no Android or desktop UI imports;
- no persistence;
- no platform locale formatting;
- no network calls;
- no mutable global state;
- explicit finite/range validation;
- version evidence/reference profiles when thresholds change.

The shared module targets Android, JVM/Desktop, `iosArm64`, and `iosSimulatorArm64`. Shared-domain changes must remain valid for all targets.

Production responsibilities are intentionally small:

- `Bmi.kt` — adult BMI/reference profile/evidence result behavior;
- `Units.kt` — unit/input models and deterministic conversions;
- `Validation.kt` — shared finite/range validation and unit-specific error contracts;
- `WaistToHeight.kt` — adult waist-to-height calculation/result behavior.

Do not move these rules into platform UI code just to simplify a screen.

### Android application

Put Android-specific behavior under:

`androidApp/src/main/java/io/github/sanskarin/healthmetric/`

Separate:

- `data/` for Android-local models, DataStore persistence, backup IO, and privacy-safe logging;
- `ui/` for application state and Compose shell wiring;
- `ui/components/` for reusable presentation components;
- `ui/format/` for locale-aware presentation parsing/formatting and chart normalization;
- `ui/screens/` for feature screens;
- `ui/state/` for defensive saved-state decoding;
- `ui/testing/` for stable UI automation tags;
- `ui/theme/` for visual design tokens/theme configuration.

`MainActivity.kt` is the Android entry point. `HealthMetricViewModel.kt` exposes persisted state/actions to the UI. `HealthMetricApp.kt` owns top-level navigation, adult/onboarding state presentation, platform document/share launchers, dialogs/snackbars, and screen composition.

Android-specific health formulas/thresholds do not belong here; call `shared`.

### Android tests

Use JVM tests under `androidApp/src/test/` for deterministic Android-module utilities that do not need framework/device behavior, including:

- preference/retention policies;
- bounded stream IO;
- locale parsing/formatting;
- chart normalization;
- saved-enum fallback.

Use instrumentation under `androidApp/src/androidTest/` for:

- Compose UI journeys;
- Android DataStore persistence/restore semantics;
- app navigation/back handling;
- document/settings behavior that requires Android runtime integration;
- release screenshot evidence.

Do not duplicate shared-domain formula tests in Android unless the regression is specifically in Android presentation/integration.

### Desktop application

Put desktop-specific behavior under:

`desktopApp/src/main/kotlin/io/github/sanskarin/healthmetric/desktop/`

Current ownership:

- `Main.kt` — window, adult-use gate, navigation, forms, transient result state, theme state, evidence/about UI, explicit external links;
- `DesktopCalculations.kt` — presentation adapter around shared calculators/validation;
- `DesktopNumbers.kt` — strict desktop measurement text parsing.

Desktop rules:

- do not duplicate BMI thresholds/formulas/reference bands;
- call the `shared` module for calculation/validation/evidence metadata;
- keep the under-18 path isolated from adult reference results;
- keep measurements/results/adult selection/theme/navigation in memory only;
- do not introduce persistence, backup, synchronization, accounts, or analytics without a reviewed architecture/privacy change;
- do not perform automatic network requests;
- external URLs require explicit user action;
- add desktop tests for parsing/presentation adapters when behavior changes.

See [`desktop.md`](desktop.md) and ADR 0005.

## Documentation ownership and tracked-file coverage

HealthMetric documents every tracked file, not only production source.

- [`documentation-map.md`](documentation-map.md) says which document owns each detailed contract and which documents need review for each type of change.
- [`repository-file-reference.md`](repository-file-reference.md) lists every exact tracked path with responsibility/maintenance notes.
- `scripts/check_repository.py` runs `git ls-files` and fails if a tracked path is missing from the exhaustive file reference.

Therefore any file addition, deletion, or rename must reconcile the file reference in the same pull request. This applies equally to Kotlin, XML, YAML, Markdown, shell/PowerShell/Python, Gradle and assets.

When adding a file, do not merely list it: document why it exists, what owns it, and any privacy/safety/release boundary it participates in.

## Formatting and lint

Run:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck :desktopApp:ktlintCheck
gradle :androidApp:lintRelease
```

Format locally when needed:

```bash
gradle :shared:ktlintFormat :androidApp:ktlintFormat :desktopApp:ktlintFormat
```

Do not silence lint without documenting why.

## Complete local verification

Unix-like systems:

```bash
bash scripts/verify.sh
```

Windows PowerShell:

```powershell
.\scripts\verify.ps1
```

Set `GRADLE_BIN` if the executable is not named `gradle`. On Windows, set `PYTHON_BIN` if required.

The scripts run repository/docs audits, repository/release-tooling Python tests, Kotlin formatting checks, shared/desktop/Android tests, desktop JAR packaging, Android lint, debug/release APK assembly, and release AAB assembly.

## Testing while developing

Repository/docs/release tooling:

```bash
python3 scripts/check_repository.py
python3 scripts/check_markdown_links.py
python3 -m unittest discover -s scripts/tests -p "test_*.py"
```

Shared domain behavior:

```bash
gradle :shared:desktopTest
```

Desktop behavior:

```bash
gradle :desktopApp:ktlintCheck :desktopApp:test
gradle :desktopApp:packageUberJarForCurrentOS
```

Android JVM checks:

```bash
gradle :androidApp:testDebugUnitTest
```

Android UI/persistence behavior:

```bash
gradle :androidApp:connectedDebugAndroidTest
```

On macOS, compile Apple targets:

```bash
gradle :shared:compileKotlinIosSimulatorArm64 :shared:compileKotlinIosArm64
```

Pull requests run standard CI, API 35 Android emulator verification, Linux/Windows/macOS desktop verification, and macOS Apple-target verification in addition to security workflows.

Keep tests deterministic. Use fictional/example measurements; avoid network access, timing assumptions, and locale-sensitive selectors unless locale behavior itself is under test.

## Android local-data invariants

Changes to Android history/backup behavior must preserve these invariants unless an ADR deliberately replaces them:

- history is disabled on fresh/default state;
- raw weight, height, and waist fields are not persisted in history;
- supported retention limits are 50, 100, 250, and 500;
- local history never grows beyond the selected retention limit;
- history is normalized newest-first by timestamp before retention is applied;
- individual undo does not enable future history saving and restores chronology by timestamp;
- backup payloads are limited to 1 MiB before parsing/writing;
- unsupported top-level backup schemas are rejected;
- top-level `history` must be a JSON array before DataStore mutation;
- explicit `history: []` is a valid intentional empty-history restore;
- a non-empty history array is rejected when no valid record survives sanitation;
- malformed individual history entries can be ignored when valid neighbors survive;
- duplicate history IDs cannot reach the UI list;
- portable backup restore never changes `history_enabled`, `adult_use_confirmed`, or `onboarding_complete`;
- restore requires explicit confirmation after the file is read;
- logging never receives backup contents or measurements.

`BackupIo` is the stream boundary. `HealthMetricDataStore` must still enforce size/schema/top-level-structure/record invariants so alternate callers cannot bypass document-flow protections.

See [`backup-format.md`](backup-format.md) and ADR 0004 before changing Android backup semantics.

## Desktop data invariants

Desktop currently has an intentionally smaller data surface:

- no measurement/history persistence;
- no backup/import/export;
- no persisted adult-use choice;
- no persisted theme/navigation choice;
- no background synchronization;
- no account requirement.

If a concrete requirement needs desktop persistence, do not add it as an incidental UI change. Update/supersede ADR 0005, define stored fields/defaults/retention/deletion/migration/threat model, add tests, and update privacy/security/release docs first.

## Numeric input invariants

Shared calculations receive numeric values and remain locale-independent.

Android presentation owns locale-aware parsing/formatting through `LocalizedNumbers`.

Desktop `DesktopNumbers` accepts ordinary dot/comma decimal input and whole-number feet while rejecting malformed, signed, scientific-notation and non-finite text.

Desktop split imperial height fields represent **whole feet + remaining inches**. The remaining-inches component must be finite and in `[0, 12)`; values such as 12 or 20 remaining inches are rejected rather than silently converted into extra feet.

When changing numeric input:

- keep finite/range validation in `shared`;
- test dot and comma decimal behavior explicitly;
- test split imperial component boundaries where applicable;
- do not move calculation thresholds into platform parsers;
- keep displayed precision intentional and covered by tests.

## Data model changes

Android persistence uses Preferences DataStore and an explicit JSON backup schema.

If Android persistence changes:

1. preserve reading of the previous released format when practical;
2. increment backup `schemaVersion` for breaking portable format changes;
3. add migration/restore tests;
4. retain strict payload/history/structure limits or document a reviewed replacement;
5. keep consent/adult-gate state device-local unless a dedicated safety/privacy ADR changes that invariant;
6. update `PRIVACY.md` if stored data changes;
7. add/update an ADR for meaningful persistence/security decisions;
8. update `CHANGELOG.md` and `what_changed.md`;
9. update the exhaustive file reference if file structure changes.

## Health reference changes

Do not modify adult reference thresholds as a platform UI-only edit. Change the versioned shared reference profile, update source metadata and `reviewedOnIsoDate`, add boundary tests, and document the rationale.

Keep wording neutral and educational. Do not convert reference bands into appearance scores, body rankings, or personalized goals.

## Privacy review questions

For every feature, ask:

- Does it require storing new data?
- Can it work offline?
- Is stored data necessary?
- Can the user delete/export it where applicable?
- Is retention bounded and understandable?
- Is state truly portable, or consent/safety state that should remain local/session-only?
- Could imported content consume excessive memory or CPU?
- Could logs reveal measurements or backup content?
- Does it introduce a third-party SDK, network endpoint, or automatic external request?

Prefer less data, bounded inputs, fewer permissions, and explicit user actions.

## UI review questions

Across Android and desktop:

- Is hierarchy readable under large text/display scaling?
- Are controls clearly labeled?
- Is meaning available without color alone?
- Is adult-only access preserved?
- Is health wording neutral and non-diagnostic?
- Are external/destructive actions explicit?
- Does keyboard/focus navigation remain logical on desktop/hardware-keyboard paths?
- Does numeric input behave predictably for accepted decimal formats?

## Dependencies and workflows

Use `gradle/libs.versions.toml`. Prefer maintained dependencies from trusted sources. Dependabot updates must pass applicable CI before merge.

When updating GitHub Actions, keep workflow permissions least-privilege and do not expose repository secrets unnecessarily to pull-request code.

Shared Kotlin changes must pass JVM tests and Apple-target compilation. Desktop changes must pass the Desktop matrix. Android UI/persistence changes must pass Android instrumentation where applicable.

Workflow additions/removals are tracked-file changes and must be reflected in the exhaustive file reference and the testing/governance/release documents they affect.

## Commit strategy

Use small meaningful commits and Conventional Commit prefixes. Do not create empty commits or churn solely to inflate commit count.
