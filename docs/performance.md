# Performance

## Performance goals

HealthMetric's calculations are constant-time arithmetic; performance risk is primarily UI/persistence and bounded backup overhead rather than computation.

Initial budgets/invariants:

- calculator result should appear in the same interaction frame without artificial delay;
- no network request may be required for calculator execution;
- history retention defaults to 100 and is user-selectable up to a hard maximum of 500 entries;
- history chart renders at most the 20 most recent entries of the selected calculator type;
- backup reads/writes are capped at 1 MiB;
- DataStore work runs through coroutine-backed APIs rather than blocking the UI thread;
- app startup should avoid network initialization or heavyweight SDK bootstrapping.

## Current design choices

- Shared calculations allocate only small result/reference objects.
- No remote API call is part of calculation.
- History list persistence is bounded by the selected retention policy.
- Lowering retention immediately trims older records, preventing stale oversized local state.
- Chart point count is bounded independently of history retention.
- UI lists use `LazyColumn`.
- Backup input is streamed with a byte counter before JSON parsing.
- Backup output size is checked before writing.
- Imported history is capped before persistence/display.
- External support/funding/release links open only after explicit user action.
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

The current history representation rewrites a bounded JSON array in Preferences DataStore. This is acceptable for the 500-entry hard ceiling and lower selectable limits. If history requirements grow significantly, migrate to a structured database with explicit migrations/indexes instead of increasing the preference payload indefinitely.

## Backup processing

The 1 MiB payload ceiling is both a security and performance boundary. Any proposal to increase it should include measured memory/CPU evidence and an updated threat/privacy review.

Malformed records are handled independently and processing stops once the maximum supported valid history size has been collected.

## Memory

HealthMetric should avoid retaining Activities/Contexts outside lifecycle-safe owners. `HealthMetricDataStore` receives the application context through the ViewModel.

Compose form values/results are screen-local. Backup JSON is retained only for the shortest necessary operation: selected restore content remains in memory until confirm/cancel, while file export generates the current JSON after a destination is selected rather than holding a pre-launch payload across the picker.

## Release review

For release candidates:

- inspect startup for obvious main-thread stalls;
- scroll a full 500-entry history list;
- switch themes/screens repeatedly;
- save/share/restore a maximum-size valid synthetic backup;
- attempt an oversized backup and confirm it is rejected safely;
- review memory for unbounded growth;
- add a macrobenchmark only if evidence shows a meaningful performance risk.
