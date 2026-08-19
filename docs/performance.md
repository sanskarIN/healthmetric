# Performance

## Performance goals

HealthMetric calculations are constant-time arithmetic. Performance risk is primarily UI/persistence, bounded backup processing, startup, chart rendering, and desktop packaging/runtime overhead rather than computation.

Cross-platform invariants:

- calculator results should appear without artificial delay;
- no network request is required for calculator execution;
- shared calculations remain small deterministic operations;
- startup avoids ad/analytics/backend SDK initialization;
- user-controlled collections/payloads remain bounded;
- optimization complexity is added only when measurements justify it.

## Shared-domain cost

BMI, waist-to-height, validation, and conversion operations are deterministic constant-size calculations. The shared domain should not acquire database, network, platform UI, or unbounded collection work.

Seeded property-style tests exercise many valid inputs for correctness; they are verification work, not runtime behavior.

## Android budgets and design choices

- history retention defaults to 100 and is user-selectable up to a hard maximum of 500 entries;
- history chart renders at most the 20 most recent entries of the selected calculator type;
- chart coordinates use finite-safe normalization so extreme finite imported values cannot overflow naive range arithmetic;
- backup reads/writes are capped at 1 MiB;
- DataStore work uses coroutine-backed APIs rather than blocking UI interactions;
- lowering retention immediately trims older records;
- UI history lists use `LazyColumn`;
- backup input is streamed with a byte counter before JSON parsing;
- backup output size is checked before writing;
- restore validates schema/top-level structure/content before opening the DataStore mutation;
- imported history is sorted/bounded before persistence/display;
- external support/funding/release links open only after explicit user action;
- no ad/analytics SDK startup cost exists.

A structurally invalid or non-empty all-invalid backup is rejected before persistence mutation. This is primarily a correctness/security rule, but it also prevents unnecessary DataStore writes for data that cannot produce a valid restore.

## Android chart arithmetic

Imported schema-v1 history accepts finite numeric calculation values after record sanitation. A finite value can still be extremely large, so subtracting raw `max - min` can overflow.

`ChartScale` normalizes through a scale-first finite-safe path before Canvas coordinates are derived. Do not replace this with raw range arithmetic or silently rewrite persisted values to make rendering easier.

Chart rendering remains bounded by the recent-entry display limit and is supplementary to the text/list representation.

## Desktop budgets and design choices

The desktop client intentionally avoids persistence/database/backup overhead.

- calculator inputs/results are transient Compose state;
- adult-use, navigation, and theme choices are transient state;
- `DesktopCalculations` delegates arithmetic/validation to the shared core;
- `DesktopNumbers` performs small bounded text-normalization/parsing operations;
- split imperial remaining-inch validation is constant-time presentation work;
- calculator screens use standard Compose layouts and bounded scrolling content;
- there is no desktop history list/chart/background synchronization;
- external links launch only after explicit user action.

The desktop client should not introduce caching, persistence hydration, background synchronization, or account initialization merely to mirror Android.

## Repository/release tooling cost

Repository verification intentionally favors deterministic correctness over micro-optimization.

- `scripts/check_repository.py` runs `git ls-files` once and compares the bounded repository path set with the exhaustive file reference;
- `scripts/check_markdown_links.py` scans local documentation links without network requests;
- release staging verifies a small fixed set of build outputs;
- final release verification expects exactly eight binary assets and hashes them once to produce `SHA256SUMS.txt`.

These checks run during development/CI/release, not application runtime.

## Measurement approach

Do not add optimization complexity without evidence.

For Android, reproduce on a release/profileable build, capture Android Studio CPU/memory/system trace evidence, identify the hot path, add a stable benchmark where useful, then compare before/after evidence.

For desktop, reproduce with a packaged/release-equivalent build on the affected operating system, distinguish JVM startup/Compose rendering/shared calculation cost, use profiler or OS activity evidence where useful, and verify improvements across desktop CI platforms.

For repository/release tooling, first reproduce the slow operation on a clean checkout and preserve deterministic/fail-closed behavior before optimizing file scans or hashing.

## Startup

A future Android baseline-profile/macrobenchmark module is justified only if profiling shows meaningful startup or scrolling cost. It is intentionally not added solely to increase project size.

Desktop should not add background initialization, account/network bootstrapping, or persistence hydration without a concrete requirement.

## Persistence

Android history rewrites a bounded JSON array in Preferences DataStore. This is acceptable for the 500-entry hard ceiling. If requirements grow significantly, migrate to a structured database with explicit migrations/indexes rather than increasing preference payloads indefinitely.

Desktop currently has no persistence. A future persistence proposal must include privacy, migration, and measured performance implications before implementation.

## Backup processing

The Android 1 MiB payload ceiling is both a security and performance boundary. Any proposal to increase it should include measured memory/CPU evidence and an updated threat/privacy review.

Restore parses one bounded JSON document, validates the required top-level history container, sanitizes records, deduplicates identifiers, sorts accepted entries newest-first, and applies a hard maximum of 500 records. Preserve those bounds before adding schema complexity.

Desktop does not currently parse HealthMetric backup files.

## Memory

Android should avoid retaining Activities/Contexts outside lifecycle-safe owners. `HealthMetricDataStore` receives application-scoped context through the ViewModel path. Backup JSON is retained only for the shortest necessary operation.

Desktop form/adult/theme/navigation/result state remains scoped to the application composition and is released with the process. Do not introduce static/global measurement caches.

## Release review

### Android

- inspect startup for obvious main-thread stalls;
- scroll a full 500-entry history list;
- exercise the 20-point chart with ordinary and extreme finite synthetic values;
- switch themes/screens repeatedly;
- save/share/restore a maximum-size valid synthetic backup;
- attempt an oversized backup and confirm safe rejection;
- attempt structurally invalid/all-invalid synthetic backups and confirm no local mutation;
- review memory for unbounded growth;
- add macrobenchmarks only if evidence shows a meaningful risk.

### Desktop

On each platform being published:

- launch the packaged/runnable artifact repeatedly;
- switch calculator sections/unit systems/themes repeatedly;
- calculate representative fictional values repeatedly;
- exercise valid/invalid split imperial height components;
- resize/scroll long screens;
- verify closing/reopening clears transient state;
- inspect for obvious runaway CPU/memory behavior;
- investigate with profiling evidence before introducing optimization complexity.

Build/package success is not a performance benchmark; use real runtime evidence before making performance claims.
