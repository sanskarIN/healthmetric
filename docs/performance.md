# Performance

## Performance goals

HealthMetric calculations are constant-time arithmetic. Performance risk is primarily UI startup/rendering, platform packaging/runtime behavior, Android persistence, and bounded backup handling rather than calculator computation.

Shared invariants:

- calculator result should appear from local arithmetic without artificial network delay;
- no remote API is required for BMI or waist-to-height execution;
- form/result state remains bounded;
- cross-platform clients must not add heavyweight startup SDKs merely for basic calculations;
- build/package regressions should be detected independently on Windows, macOS, Linux, browser, iOS, and Android CI paths.

Android-specific bounded-data invariants:

- history retention defaults to 100 and is user-selectable up to 500 entries;
- history chart renders at most the 20 most recent entries of the selected calculator type;
- backup reads/writes are capped at 1 MiB;
- DataStore work uses coroutine-backed APIs rather than intentional UI-thread blocking.

## Shared domain

The `shared` module performs small numeric calculations and allocates only small result/reference objects.

Performance changes to domain code should preserve:

- deterministic behavior;
- finite/range validation;
- no platform/network dependency;
- no unbounded collections/caches;
- equivalent results across all targets.

Micro-optimization of BMI arithmetic is not a useful goal unless measurement demonstrates an actual issue.

## Shared Compose client

The iOS, desktop, and browser clients use the same common calculator presentation.

Current state is intentionally small:

- one adult-use gate;
- two calculator screens/modes;
- bounded text input;
- local Compose state;
- no persistent history list;
- no remote data loading;
- no startup analytics/advertising SDK.

Performance review should focus on first interaction, text-field responsiveness, resize/recomposition behavior, and avoiding platform-specific dependency growth.

## Desktop

Windows/macOS/Linux clients run the Compose Desktop JVM application.

Release review should check:

- startup reaches usable calculator UI promptly on representative hardware;
- window resizing does not cause obvious stalls;
- repeated metric/imperial and calculator switching remains responsive;
- native packages launch without repeated expensive initialization;
- memory does not grow continually during ordinary calculator use.

Do not add native packaging complexity as a performance workaround. Profile the running application first.

## Browser

Web performance must consider both Wasm and JavaScript compatibility output.

Review:

- initial static asset load size/time;
- time from page load to usable adult-use gate;
- first text-field interaction;
- calculator interaction latency;
- resizing/orientation changes;
- browser memory across repeated calculations;
- JavaScript fallback behavior on environments that do not use the preferred Wasm path.

The calculator does not require a server round trip, so calculation latency should not depend on network conditions once the application assets have loaded.

When bundle size becomes a concern, measure generated production distributions before adding custom splitting/minification complexity.

## iOS/iPadOS

The SwiftUI host embeds a static Kotlin/Compose framework.

Review on simulator and representative hardware when possible:

- cold/warm launch behavior;
- first Compose frame;
- text-field responsiveness;
- orientation/iPad resizing behavior;
- memory across repeated navigation/calculations;
- no repeated framework initialization caused by host lifecycle mistakes.

The Xcode simulator build in CI is a build/integration gate, not a runtime performance benchmark.

## Android current design choices

- No remote API call is part of calculation.
- History persistence is bounded by the selected retention policy.
- Lowering retention immediately trims older records.
- Chart point count is bounded independently of history retention.
- UI history lists use lazy rendering.
- Backup input is streamed with a byte counter before JSON parsing.
- Backup output size is checked before writing.
- Imported history is capped before persistence/display.
- External support/funding/release links open only after explicit user action.
- No ad/analytics SDK startup cost exists.

## Android persistence

The current history representation rewrites a bounded JSON array in Preferences DataStore. This is acceptable for the 500-entry hard ceiling and lower selectable limits.

If Android history requirements grow significantly, prefer a structured database with explicit migrations/indexes rather than increasing a preferences payload indefinitely.

If persistence is generalized to iOS/desktop/web, create a platform-appropriate shared contract and benchmark the chosen implementation instead of assuming Android DataStore performance characteristics apply elsewhere.

## Android backup processing

The 1 MiB payload ceiling is both a security and performance boundary. Any proposal to increase it requires measured memory/CPU evidence plus updated threat/privacy review.

Malformed records are handled independently and processing remains bounded by the maximum supported valid history size.

## Memory

General rules:

- avoid unbounded caches/collections;
- keep form/result state scoped to the relevant UI lifecycle;
- avoid retaining native platform contexts/controllers beyond lifecycle owners;
- do not retain backup data longer than necessary;
- do not log/store raw measurements solely for profiling.

Android-specific behavior:

- `HealthMetricDataStore` receives application context through lifecycle-safe ownership;
- selected restore content is held only until confirm/cancel;
- file export creates current JSON after destination selection rather than retaining a pre-launch payload.

## Measurement approach

Do not add optimization complexity without evidence.

For a suspected performance issue:

1. reproduce on a production-like build for the affected platform;
2. capture platform-appropriate CPU/memory/startup evidence;
3. identify the hot path;
4. add a stable benchmark/regression measurement when practical;
5. optimize;
6. compare before/after evidence;
7. verify no correctness/privacy regression.

Useful tools depend on platform:

- Android Studio profiler/system trace for Android;
- Xcode Instruments for iOS/macOS where appropriate;
- JVM/profiling tools for desktop;
- browser DevTools performance/network/memory panels for web.

## Startup optimization policy

Do not add baseline profiles, custom native launch machinery, eager caches, or large initialization frameworks simply to increase project complexity.

Android baseline-profile/macrobenchmark work is justified only if profiling shows a meaningful startup/scrolling cost.

Equivalent rule applies to other platforms: measure first, optimize second.

## CI versus performance evidence

Cross-platform CI verifies compilation and packaging:

- Windows MSI path;
- macOS DMG path;
- Linux DEB path;
- JS/Wasm production distributions;
- iOS framework + Xcode simulator application;
- Android tests/lint/builds.

These jobs protect integration/build regressions but do not replace runtime performance measurement on representative clients.

## Release review

For a release candidate:

### Shared clients

- open Android, iOS, desktop, and browser clients;
- verify startup/interactions show no obvious stalls;
- switch calculator/mode repeatedly;
- resize/rotate relevant clients;
- perform repeated calculations and watch for unbounded memory growth.

### Browser

- inspect production asset loading;
- test Wasm and JavaScript compatibility output;
- check a narrow/mobile viewport and desktop viewport.

### Android persistence

- scroll a full 500-entry synthetic history list;
- save/share/restore a maximum-size valid synthetic backup;
- attempt an oversized backup and confirm safe rejection;
- inspect memory for unbounded growth.

Add dedicated benchmarks only when evidence shows a meaningful risk worth protecting automatically.
