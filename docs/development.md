# Development Guide

## Working agreements

HealthMetric favors small, reviewable changes and a deterministic shared domain layer.

Before editing:

1. read `what_changed.md`;
2. inspect recent commits and open issues;
3. identify the smallest module that owns the behavior;
4. add or update tests with the behavior change.

## Module ownership

### Shared domain

Put platform-neutral calculation behavior under:

`shared/src/commonMain/kotlin/io/github/sanskarin/healthmetric/domain/`

Rules:

- no Android imports;
- no persistence;
- no platform locale formatting;
- no network calls;
- no mutable global state;
- explicit validation;
- version evidence/reference profiles when thresholds change.

The shared module currently targets Android, JVM/Desktop, `iosArm64`, and `iosSimulatorArm64`. Shared-domain changes must remain valid for all targets.

### Android application

Put Android-specific behavior under:

`androidApp/src/main/java/io/github/sanskarin/healthmetric/`

Separate:

- `data/` for local persistence models, backup IO, and privacy-safe logging;
- `ui/` for app state and Compose UI;
- `ui/components/` for reusable presentation components;
- `ui/format/` for locale-aware presentation parsing/formatting;
- `ui/screens/` for feature screens;
- `ui/testing/` for stable UI automation tags;
- `ui/theme/` for visual design tokens/theme configuration.

## Formatting and lint

Run:

```bash
gradle :shared:ktlintCheck :androidApp:ktlintCheck
gradle :androidApp:lintRelease
```

Format locally when needed:

```bash
gradle :shared:ktlintFormat :androidApp:ktlintFormat
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

Set `GRADLE_BIN` if the executable is not named `gradle`.

## Testing while developing

Shared domain behavior:

```bash
gradle :shared:desktopTest
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

Pull requests run standard CI, an API 35 emulator workflow, and a macOS Apple-target workflow. Keep direct-screen Compose tests deterministic: avoid real health data, network access, timing assumptions, and locale-sensitive selectors unless locale behavior itself is under test.

## Local data invariants

Changes to history/backup behavior must preserve these invariants unless an ADR deliberately replaces them:

- history is disabled on fresh/default state;
- raw weight, height, and waist fields are not persisted in history;
- supported retention limits are 50, 100, 250, and 500;
- local history never grows beyond the selected retention limit;
- individual undo does not enable future history saving;
- backup payloads are limited to 1 MiB before parsing/writing;
- unsupported top-level backup schemas are rejected;
- malformed history entries are ignored individually;
- duplicate history IDs cannot reach the UI list;
- portable backup restore never changes `history_enabled`, `adult_use_confirmed`, or `onboarding_complete`;
- restore requires explicit confirmation after the file is read;
- logging never receives backup contents or measurements.

`BackupIo` is the stream boundary. `HealthMetricDataStore` must still enforce size/schema/record invariants so alternate callers cannot bypass document-flow protections.

See [`backup-format.md`](backup-format.md) and ADR 0004 before changing backup semantics.

## Locale/numeric invariants

Shared calculations must receive numeric values and remain locale-independent. Android presentation owns locale parsing/formatting through `LocalizedNumbers`.

When changing numeric input:

- keep finite/range validation in the domain layer;
- allow at most one decimal separator;
- test dot and comma decimal behavior explicitly;
- do not interpret grouping separators as measurement decimal data;
- keep displayed history/result precision intentional and covered by tests.

## Data model changes

Current persistence uses Preferences DataStore and an explicit JSON backup schema.

If persistence structure changes:

1. preserve reading of the previous released format when practical;
2. increment backup `schemaVersion` for breaking portable format changes;
3. add migration/restore tests;
4. retain strict payload/history limits or document a reviewed replacement;
5. keep consent/adult-gate state device-local unless a dedicated safety/privacy ADR explicitly changes that invariant;
6. update `PRIVACY.md` if stored data changes;
7. add/update an ADR for meaningful persistence/security decisions;
8. update `CHANGELOG.md` and `what_changed.md`.

## Health reference changes

Do not modify adult reference thresholds as a UI-only edit. Change the versioned shared reference profile, update its source metadata and `reviewedOnIsoDate`, add boundary tests, and document the rationale.

Keep wording neutral and educational. Do not convert reference bands into appearance scores, body rankings, or personalized goals.

## Privacy review questions

For every feature, ask:

- Does it require storing new data?
- Can it work offline?
- Is the stored data necessary?
- Can the user delete/export it?
- Is retention bounded and understandable?
- Is the state truly portable, or is it consent/safety state that must remain device-local?
- Could imported content consume excessive memory or CPU?
- Could logs reveal measurements or backup content?
- Does it introduce a third-party SDK or network endpoint?

Prefer the design with less data, bounded inputs, fewer permissions, and explicit user actions.

## UI review questions

- Is the hierarchy readable at large font sizes?
- Are touch targets comfortably sized?
- Are controls screen-reader labeled?
- Is meaning available without color alone?
- Does a destructive/replacement action have appropriate confirmation/undo?
- Does the layout remain usable on wider screens?
- Does numeric input/display behave predictably in comma- and dot-decimal locales?
- Do automation tags supplement rather than replace accessible semantics?

## Dependencies and workflows

Use the version catalog in `gradle/libs.versions.toml`. Prefer maintained libraries from AndroidX/Kotlin/other trusted sources. Dependabot updates must pass CI before merge.

When updating GitHub Actions, check the action owner's current supported major version and keep workflow permissions least-privilege. Pull-request code must never receive unnecessary write permissions or repository secrets.

Shared Kotlin changes must pass both JVM tests and Apple-target compilation before release.

## Commit strategy

Use small meaningful commits and Conventional Commit prefixes. Do not create empty commits or churn solely to inflate commit count.
