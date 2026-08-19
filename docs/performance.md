# Performance

## Performance goals

HealthMetric calculations are constant-time arithmetic. Performance risk is primarily UI/persistence, bounded backup processing, startup, and desktop packaging/runtime overhead rather than computation.

Cross-platform invariants:

- calculator results should appear without artificial delay;
- no network request is required for calculator execution;
- shared calculations remain small deterministic operations;
- startup avoids ad/analytics/backend SDK initialization;
- optimization complexity is added only when measurements justify it.

## Android budgets and design choices

- history retention defaults to 100 and is user-selectable up to a hard maximum of 500 entries;
- history chart renders at most the 20 most recent entries of the selected calculator type;
- backup reads/writes are capped at 1 MiB;
- DataStore work uses coroutine-backed APIs rather than blocking UI interactions;
- lowering retention immediately trims older records;
- UI history lists use `LazyColumn`;
- backup input is streamed with a byte counter before JSON parsing;
- backup output size is checked before writing;
- imported history is capped before persistence/display;
- external support/funding/release links open only after explicit user action;
- no ad/analytics SDK startup cost exists.

## Desktop budgets and design choices

The desktop client intentionally avoids persistence/database/backup overhead.

- calculator inputs/results are transient Compose state;
- adult-use, navigation, and theme choices are transient state;
- `DesktopCalculations` delegates arithmetic/validation to the shared core;
- `DesktopNumbers` performs small bounded text-normalization/parsing operations;
- calculator screens use standard Compose layouts and bounded scrolling content;
- there is no desktop history list/chart/background synchronization;
- external links launch only after explicit user action.

## Measurement approach

Do not add optimization complexity without evidence.

For Android, reproduce on a release/profileable build, capture Android Studio CPU/memory/system trace evidence, identify the hot path, add a stable benchmark where useful, then compare before/after evidence.

For desktop, reproduce with a packaged/release-equivalent build on the affected operating system, distinguish JVM startup/Compose rendering/shared calculation cost, use profiler or OS activity evidence where useful, and verify improvements across desktop CI platforms.

## Startup

A future Android baseline-profile/macrobenchmark module is justified only if profiling shows meaningful startup or scrolling cost. It is intentionally not added solely to increase project size.

Desktop should not add background initialization, account/network bootstrapping, or persistence hydration without a concrete requirement.

## Persistence

Android history rewrites a bounded JSON array in Preferences DataStore. This is acceptable for the 500-entry hard ceiling. If requirements grow significantly, migrate to a structured database with explicit migrations/indexes rather than increasing preference payloads indefinitely.

Desktop currently has no persistence. A future persistence proposal must include privacy, migration, and measured performance implications before implementation.

## Backup processing

The Android 1 MiB payload ceiling is both a security and performance boundary. Any proposal to increase it should include measured memory/CPU evidence and an updated threat/privacy review.

Desktop does not currently parse HealthMetric backup files.

## Memory

Android should avoid retaining Activities/Contexts outside lifecycle-safe owners. `HealthMetricDataStore` receives the application context through the ViewModel. Backup JSON is retained only for the shortest necessary operation.

Desktop form/adult/theme/navigation/result state remains scoped to the application composition and is released with the process. Do not introduce static/global measurement caches.

## Release review

### Android

- inspect startup for obvious main-thread stalls;
- scroll a full 500-entry history list;
- switch themes/screens repeatedly;
- save/share/restore a maximum-size valid synthetic backup;
- attempt an oversized backup and confirm safe rejection;
- review memory for unbounded growth;
- add macrobenchmarks only if evidence shows a meaningful risk.

### Desktop

On each platform being published:

- launch the packaged/runnable artifact repeatedly;
- switch calculator sections/unit systems/themes repeatedly;
- calculate representative fictional values repeatedly;
- resize/scroll long screens;
- verify closing/reopening clears transient state;
- inspect for obvious runaway CPU/memory behavior;
- investigate with profiling evidence before introducing optimization complexity.
