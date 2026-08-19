# Accessibility

HealthMetric treats accessibility as a product requirement, not a release polish step.

## Current implementation

- Semantic headings are used on primary screens.
- Navigation items include text labels.
- Interactive icons have content descriptions where the icon has standalone meaning.
- Material components provide touch targets and focus behavior.
- Text uses scalable Compose typography rather than fixed pixel sizes.
- Light, dark, and system themes are supported.
- Measurement-chart meaning is not encoded as health colors.
- The chart exposes a textual content description summarizing count, start/end, minimum, and maximum values.
- Empty/error states contain text rather than relying on icons alone.
- Health/body wording is neutral and avoids appearance ranking.

## Manual release checks

### TalkBack

Verify:

- onboarding is read in a logical order;
- all input labels are announced;
- bottom navigation destinations are distinct;
- result text is understandable without visual layout;
- chart description is announced once and is meaningful;
- destructive actions are clearly named.

### Text scaling

Test at large system font/display sizes. Inputs, buttons, cards, and dialogs must remain usable without clipped critical text.

### Theme/contrast

Review light and dark themes, including Android dynamic color. Ensure validation errors remain distinguishable by text/icon/context and not color alone.

### Keyboard/DPAD

Where a hardware keyboard or DPAD is available, verify logical focus traversal through fields, buttons, chips, navigation, and dialogs.

### Motion

The current app does not rely on decorative motion. If substantial animations are added later, provide a reduced-motion path and avoid motion that blocks content access.

## Charts

Charts are supplementary. Every chart must have an equivalent textual summary and nearby list entries so a chart is never the only way to access measurement information.

## Language

Avoid:

- appearance-shaming labels;
- body rankings;
- congratulatory/negative judgments tied to measurements;
- pressure-oriented body goals.

Prefer factual adult reference descriptions with clear non-diagnostic context.

## Future automation

Roadmap items include broader Compose semantics tests and accessibility-scanner evidence in release artifacts.

## Reporting accessibility issues

Open a bug report with the affected screen, assistive technology, Android version, reproduction steps, and expected behavior. Use fictional/example measurement data only.
