# Accessibility

HealthMetric treats accessibility as a product requirement, not a release polish step.

## Current implementation

- Semantic headings are used on primary screens.
- Navigation items include text labels.
- Interactive icons have content descriptions where the icon has standalone meaning.
- Per-entry history delete buttons expose explicit accessible names.
- Material components provide touch targets and focus behavior.
- Text uses scalable Compose typography rather than fixed pixel sizes.
- Light, dark, and system themes are supported.
- Measurement-chart meaning is not encoded as health colors.
- The chart exposes a textual content description summarizing count, start/end, minimum, and maximum values.
- Empty/error states contain text rather than relying on icons alone.
- Destructive erase/delete flows use explicit confirmation dialogs.
- Backup restore requires a confirmation dialog before local portable data is replaced.
- Individual history deletion provides a text-labeled Undo action.
- Calculator and history numbers use locale-aware display formatting.
- Health/body wording is neutral and avoids appearance ranking.
- Wider Android windows use a centered bounded content width rather than stretching controls indefinitely.

## Manual release checks

### TalkBack

Verify:

- onboarding is read in a logical order;
- all input labels are announced;
- bottom navigation destinations are distinct;
- result text is understandable without visual layout;
- chart description is announced once and is meaningful;
- each history entry's delete action is clearly associated with the entry;
- Undo is announced and actionable after an individual deletion;
- restore/erase/delete confirmation dialogs announce their title, purpose, confirm action, and cancel action;
- settings retention choices are understandable without depending on position or color.

### Text scaling

Test at large system font/display sizes. Inputs, buttons, cards, chips, history rows, snackbars, and dialogs must remain usable without clipped critical text.

Retention choices and bottom navigation should be checked at the largest supported font/display combinations because compact multi-control rows are more likely to need layout adjustment.

### Theme/contrast

Review light and dark themes, including Android dynamic color. Ensure validation errors remain distinguishable by text/context and not color alone.

Do not assign health meaning solely through chart or status color.

### Keyboard/DPAD

Where a hardware keyboard or DPAD is available, verify logical focus traversal through fields, buttons, chips, navigation, history delete actions, snackbars, and dialogs.

### Locale/numeric presentation

Use at least one dot-decimal locale and one comma-decimal locale. Verify:

- decimal keyboard/input accepts an expected decimal separator;
- calculator results are announced with the displayed localized number;
- history values and chart summaries use consistent formatting;
- no control becomes ambiguous because of translated/expanded text when additional locales are introduced.

### Motion

The current app does not rely on decorative motion. If substantial animations are added later, provide a reduced-motion path and avoid motion that blocks content access.

## Charts

Charts are supplementary. Every chart must have an equivalent textual summary and nearby list entries so a chart is never the only way to access measurement information.

The screen-reader summary must continue to describe the measurement type, number of plotted values, and meaningful range/start/end information without assigning appearance or diagnosis meaning.

## Language

Avoid:

- appearance-shaming labels;
- body rankings;
- congratulatory/negative judgments tied to measurements;
- pressure-oriented body goals.

Prefer factual adult reference descriptions with clear non-diagnostic context.

## Automated coverage

Current Compose instrumentation covers onboarding/adult gating, BMI and ratio success/error flows, settings actions, history deletion/confirmation, and persistence/backup behavior. Stable automation tags supplement—but never replace—user-facing accessibility semantics.

Automated tests cannot replace manual TalkBack, large-text, contrast, keyboard/DPAD, and device checks.

## Release evidence still required

Before the first public release candidate, record:

- TalkBack walkthrough result;
- large-font/display result;
- light/dark/dynamic-color review result;
- keyboard/DPAD review where available;
- representative screenshot evidence using fictional measurements only.

## Reporting accessibility issues

Open a bug report with the affected screen, assistive technology, Android version, reproduction steps, and expected behavior. Use fictional/example measurement data only.
