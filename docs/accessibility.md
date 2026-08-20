# Accessibility

HealthMetric treats accessibility as a product requirement, not a release-polish step. The same adult-only, neutral, non-diagnostic experience must remain usable on Android, iPhone/iPad, Windows, macOS, Linux, and the browser clients.

## Shared accessibility principles

Every client should preserve these requirements:

- meaningful text rather than color-only health interpretation;
- scalable text/layout behavior;
- keyboard/focus support where the platform exposes keyboard navigation;
- screen-reader semantics from standard Compose/Material controls;
- sufficiently large touch/click targets;
- clear input labels and validation messages;
- no appearance ranking, body-shaming, or pressure-oriented targets;
- adult-use notice understandable without relying on visual styling;
- calculator result understandable as text without requiring chart/color interpretation;
- layout remains usable at narrow phone widths and resized desktop/browser windows.

The shared Compose client uses Material components and semantic text labels rather than custom canvas-only controls for its calculator interactions.

## Shared calculator client

The iOS, desktop, and browser clients currently provide:

- adult-use gate;
- BMI/waist-to-height navigation;
- metric/imperial selector;
- labeled measurement fields;
- calculate actions;
- text validation errors;
- text result cards;
- neutral educational notices.

Manual review should verify that focus/reading order follows this logical sequence and that window resizing/text scaling does not hide the adult-use notice or calculator actions.

## Android-specific implementation

The mature Android client additionally includes accessibility behavior for history, settings, backup, dialogs, and theming:

- semantic headings on primary screens;
- text labels on navigation items;
- standalone interactive icons with content descriptions;
- explicit names for per-entry history delete buttons;
- scalable Compose typography;
- light/dark/system themes plus Android dynamic color;
- chart meaning not encoded as health colors;
- textual chart summary describing count/start/end/minimum/maximum;
- text-based empty/error states;
- confirmation for destructive erase/delete/restore flows;
- text-labeled Undo after individual history deletion;
- locale-aware calculator/history formatting;
- centered bounded content width on wider Android windows.

## Android manual checks — TalkBack

Verify:

- onboarding is read in a logical order;
- all input labels are announced;
- navigation destinations are distinct;
- result text is understandable without visual layout;
- chart description is announced once and is meaningful;
- each history delete action is clearly associated with its entry;
- Undo is announced/actionable;
- restore/erase/delete dialogs announce title, purpose, confirm, and cancel actions;
- retention choices are understandable without relying on position/color.

## iOS/iPadOS manual checks — VoiceOver

On a simulator/physical device with appropriate accessibility testing available, verify:

- adult-use notice is read in logical order;
- the confirmation button has an understandable label;
- calculator/measurement-system selectors are distinguishable;
- every measurement field announces its label/value;
- validation errors and result text are discoverable after calculation;
- orientation and iPad window sizing do not hide critical controls;
- keyboard focus is usable when an external keyboard is attached.

The SwiftUI host embeds Compose content; accessibility review must therefore validate the resulting integrated app rather than assuming the bridge itself guarantees perfect VoiceOver behavior.

## Desktop manual checks

On Windows, macOS, and Linux verify:

- logical Tab/Shift+Tab traversal;
- visible focus indication from standard controls;
- Enter/Space activates focused buttons as expected by the platform;
- window resizing does not clip the adult-use gate or result card;
- system/display scaling keeps controls readable/clickable;
- narrow windows remain scrollable rather than hiding content;
- screen-reader behavior is reviewed with representative platform tooling where practical.

Desktop packages share the same Compose UI, but each host should still receive a smoke accessibility review because native accessibility bridges differ by operating system.

## Browser manual checks

For both Wasm and JavaScript/compatibility output verify:

- browser zoom does not make critical controls inaccessible;
- keyboard focus order is logical;
- no interaction requires pointer-only input;
- narrow/mobile viewport remains scrollable;
- text is readable in high-contrast/system modes where supported;
- adult-use notice and result/error text remain available to assistive technology;
- fallback JavaScript deployment does not regress the interaction order compared with the Wasm build.

## Text scaling and resizing

Across platforms, test larger text/display scaling and smaller available windows.

Critical content that must remain reachable:

- adult-use safety text;
- confirmation button;
- calculator selectors;
- measurement fields;
- Calculate button;
- validation messages;
- result/educational notice.

Android release candidates should additionally test history rows, snackbars, retention controls, dialogs, and bottom navigation at large font/display settings.

## Theme and contrast

Review light/dark system environments where supported. Android additionally supports dynamic color.

Validation/error meaning and calculator interpretation must never depend solely on color.

Do not assign positive/negative appearance meaning to a health reference band through celebratory/alarming styling.

## Numeric presentation

Android should be checked in at least one dot-decimal and one comma-decimal locale.

The shared Compose client currently accepts practical dot/comma decimal input; manually verify field editing and result readability on representative targets.

Numeric formatting changes must not alter shared calculator arithmetic.

## Motion

The current clients do not rely on substantial decorative motion. If future animation becomes meaningful:

- respect platform reduced-motion preferences where APIs make that practical;
- do not require animation to understand or operate the calculator;
- avoid motion that delays access to health information.

## Charts

The current shared iOS/desktop/web calculator client does not use history charts.

Android charts are supplementary. Every chart must retain an equivalent textual summary and nearby list entries so the chart is never the only way to access measurement information.

## Automated coverage

Android instrumentation currently covers onboarding/adult gating, BMI/ratio success/error flows, settings actions, history deletion/confirmation, and persistence/backup behavior. Stable automation tags supplement—but never replace—user-facing accessibility semantics.

Cross-platform CI currently verifies that iOS, desktop, and browser clients compile/package. That is not a substitute for manual screen-reader/focus/zoom testing.

Future stable multiplatform UI tests should focus on shared interaction invariants without duplicating the calculation-domain tests.

## Release accessibility evidence

Before a production release candidate, record representative results for:

- Android TalkBack;
- Android large-font/display review;
- Android light/dark/dynamic-color review;
- iOS VoiceOver and text-size review;
- desktop keyboard/focus/resizing review on supported hosts;
- browser keyboard/zoom/narrow-viewport review for Wasm and compatibility output;
- representative screenshots using fictional/example measurements only.

Physical-device/manual accessibility evidence is intentionally separate from CI compilation and must not be falsely claimed complete when it has not been performed.

## Reporting accessibility issues

Report the affected platform/client, screen, assistive technology/input method, OS/browser version, reproduction steps, and expected behavior. Use fictional/example measurement data only.
