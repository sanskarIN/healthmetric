# Accessibility

HealthMetric treats accessibility as a product requirement, not a release polish step.

## Cross-platform principles

- Controls use visible text labels or accessible names.
- Result/error meaning is expressed in text and never color alone.
- Adult-use choices and unavailable states are understandable without visual styling.
- Health/body wording is neutral and avoids appearance ranking.
- Calculator operation must not depend on decorative animation.
- Release evidence must use fictional/example measurements only.

## Android implementation

- Semantic headings are used on primary screens.
- Navigation items include text labels.
- Interactive icons have content descriptions where the icon has standalone meaning.
- Per-entry history delete buttons expose explicit accessible names.
- Material components provide touch targets and focus behavior.
- Text uses scalable Compose typography rather than fixed pixel sizes.
- Light, dark, system, and dynamic-color themes are supported where applicable.
- Measurement-chart meaning is not encoded as health colors.
- The chart exposes a textual content description summarizing count, start/end, minimum, and maximum values.
- Empty/error states contain text rather than relying on icons alone.
- Destructive erase/delete flows use explicit confirmation dialogs.
- Backup restore requires confirmation before portable local data is replaced.
- Individual history deletion provides a text-labeled Undo action.
- Calculator/history numbers use locale-aware display formatting.
- Wider Android windows use centered bounded content width.

## Desktop implementation

- The startup adult-use gate uses explicit text buttons.
- The under-18 path presents an explicit textual unavailable explanation.
- Section navigation uses text buttons rather than icon-only destinations.
- Calculator inputs use visible `OutlinedTextField` labels.
- Metric/imperial choices use standard radio controls with adjacent text.
- Calculation failures include a text heading and validation message.
- Successful results include a value, contextual label, explanation, and educational notice.
- Light/dark theme uses a labeled standard switch.
- Project/evidence/funding links use visible text buttons.
- Major controls are standard Compose Material desktop controls that participate in keyboard focus behavior.
- No chart or color-coded health ranking is used in the desktop client.

## Android manual release checks

### TalkBack

Verify:

- onboarding is read in a logical order;
- all input labels are announced;
- bottom navigation destinations are distinct;
- result text is understandable without visual layout;
- chart description is announced once and is meaningful;
- each history entry's delete action is clearly associated with the entry;
- Undo is announced/actionable after individual deletion;
- restore/erase/delete confirmation dialogs announce title, purpose, confirm, and cancel actions;
- settings retention choices are understandable without depending on position/color.

### Text scaling

Test at large system font/display sizes. Inputs, buttons, cards, chips, history rows, snackbars, and dialogs must remain usable without clipped critical text.

Retention choices and bottom navigation require special attention at large font/display combinations.

### Theme/contrast

Review light/dark themes and Android dynamic color. Validation errors must remain distinguishable by text/context and not color alone.

### Keyboard/DPAD

Where available, verify logical focus traversal through fields, buttons, chips, navigation, history delete actions, snackbars, and dialogs.

### Locale/numeric presentation

Use at least one dot-decimal and one comma-decimal locale. Verify input, displayed result precision, history formatting, and chart descriptions remain consistent.

## Desktop manual release checks

Perform these checks on every desktop operating system that will be published.

### Keyboard and focus

Verify logical traversal through:

- adult-use buttons;
- side navigation;
- metric/imperial radio controls;
- all text fields;
- calculate buttons;
- theme switch;
- reset-adult-choice button;
- About/evidence external-link buttons.

Confirm focus remains visible and predictable after changing sections/unit systems.

### Screen reader

Using the target platform's available assistive technology, verify:

- the HealthMetric window/title is discoverable;
- adult-use choices have clear names;
- form labels are associated with their controls;
- radio choices and theme switch expose state;
- errors/results are understandable in reading order;
- external-link buttons identify their purpose;
- the under-18 path does not expose adult calculator controls.

### Display scaling and resizing

Verify:

- common high-DPI/display-scaling settings;
- window resizing near the practical minimum size;
- long educational/reference text remains reachable through scrolling;
- controls do not overlap or become unusable.

### Theme/contrast

Review light and dark desktop themes. Error/result meaning must remain available in text independent of container color.

### Process restart

Close and reopen the desktop app and confirm calculator inputs/results, adult-use choice, navigation, and theme do not persist. This is both a privacy and predictable-state check.

## Motion

The current clients do not rely on substantial decorative motion. If meaningful animations are introduced later, evaluate reduced-motion behavior and avoid motion that blocks content access.

## Android charts

Charts are supplementary. Every chart must have an equivalent textual summary and nearby list entries so a chart is never the only way to access measurement information.

The screen-reader summary must describe measurement type, count, meaningful range/start/end information without assigning appearance or diagnosis meaning.

## Language

Avoid:

- appearance-shaming labels;
- body rankings;
- congratulatory/negative judgments tied to measurements;
- pressure-oriented body goals;
- diagnostic claims from screening measurements.

Prefer factual adult reference descriptions with explicit non-diagnostic context.

## Automated coverage

Android instrumentation covers onboarding/adult gating, BMI and ratio success/error flows, settings actions, history deletion/confirmation, and persistence/backup behavior. Stable automation tags supplement—but never replace—user-facing accessibility semantics.

Desktop JVM tests cover input parsing and shared-core presentation integration. The Desktop workflow additionally verifies the client on Linux, Windows, and macOS build environments.

Automated tests cannot replace manual screen-reader, large-text/display scaling, contrast, keyboard/focus, and physical/device/platform checks.

## Release evidence still required

Before the first public release candidate, record:

### Android

- TalkBack walkthrough result;
- large-font/display result;
- light/dark/dynamic-color review result;
- keyboard/DPAD review where available;
- representative screenshots with fictional data.

### Desktop

For each platform being distributed:

- keyboard/focus walkthrough result;
- screen-reader spot-check result where available;
- display-scaling/window-resize result;
- light/dark review result;
- external-link review;
- process-restart privacy-state review;
- representative screenshots with fictional data.

## Reporting accessibility issues

Report the affected platform/screen, assistive technology, OS/version, reproduction steps, and expected behavior. Use fictional/example measurement data only.
