# Performance

## Performance goals

HealthMetric's calculations are constant-time arithmetic; performance risk is primarily UI/persistence overhead rather than computation.

Initial budgets:

- calculator result should appear in the same interaction frame without artificial delay;
- no network request may be required for calculator execution;
- history retained locally is capped at 500 entries;
- history chart renders at most the 20 most recent entries of the selected calculator type;
- DataStore work runs through coroutine-backed APIs rather than blocking the UI thread;
- app startup should avoid network initialization or heavyweight SDK bootstrapping.

## Current design choices

- Shared calculations allocate only small result/reference objects.
- No remote API call is part of calculation.
- History list persistence is bounded.
- Chart point count is bounded.
- UI lists use `LazyColumn`.
- External support/funding links open only after explicit user action.
- No ad/analytics SDK startup cost exists.

## Measurement approach

Do not add optimization complexity without evidence. If performance problems appear:

1. reproduce on a release/profileable build;
2. capture Android Studio CPU/memory/system trace evidence;
3. identify the hot path;
4. add a benchmark/regression measurement if stable;
5. optimize and compare before/after evidence.

## Startup

A future baseline profile/macrobenchmark module is justified only if profiling shows meaningful startup or scrolling cost. It is intentionally not added solely to increase project size.

## Persistence

The current history representation rewrites a bounded JSON array in Preferences DataStore. This is acceptable for the small 500-entry ceiling. If history requirements grow significantly, migrate to a structured database with explicit migrations/indexes instead of increasing the preference payload indefinitely.

## Memory

HealthMetric should avoid retaining Activities/Contexts outside lifecycle-safe owners. `HealthMetricDataStore` receives the application context through the ViewModel.

## Release review

For release candidates:

- inspect startup for obvious main-thread stalls;
- scroll a full history list;
- switch themes/screens repeatedly;
- export/restore a maximum-size synthetic history backup;
- review memory for unbounded growth.
