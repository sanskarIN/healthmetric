# Screenshot Capture Checklist

Release screenshots must come from the built Android app. Do not fabricate screenshots or commit/upload images containing real personal health information.

## Automated release-evidence capture

`ReleaseScreenshotCaptureTest` drives the real Android app on the API 35 Pixel 7 emulator used by `.github/workflows/android-instrumentation.yml`. The test resets local app state before capture, uses fictional/example values, writes PNGs to app-scoped external storage, and the workflow pulls them into the runner before uploading the `android-release-screenshots` artifact.

Required automated capture set:

1. `01-onboarding.png` — adult-use and privacy notice.
2. `02-bmi-metric.png` — metric calculator before result entry.
3. `03-bmi-result.png` — neutral educational BMI result using fictional/example measurements.
4. `04-waist-ratio.png` — waist-to-height result using fictional/example measurements.
5. `05-history.png` — local history and accessible neutral chart.
6. `06-settings.png` — privacy/data controls.
7. `07-about.png` — version, MIT license, support, GitHub, BMC, and Made by the Sanskar credit.
8. `08-dark-theme.png` — representative dark-theme settings screen.

If any required PNG is missing, the workflow artifact-upload step fails.

## Visual approval requirements

Automated capture proves that the real app can render and export the expected evidence set; it does not replace human visual review. Before permanent README/store publication:

- inspect every PNG from the exact release-candidate run;
- confirm no emulator/system overlays obscure the app;
- confirm no accidental personal/account information is visible;
- confirm text is legible and not clipped;
- confirm the result/education copy is neutral and adult-only;
- confirm light/dark presentation is coherent;
- compare the image set with the exact release candidate being tagged.

## Manual/device capture requirements

For additional physical-device or marketing captures:

- use a release/debug build from the repository, not a mockup;
- use fictional/example data only;
- capture at a common phone size; add tablet capture only if responsive layout differs materially;
- use default system font scaling for marketing captures, then separately perform large-text accessibility review;
- do not include device notifications, personal account names, private email addresses, or other personal data;
- optimize PNGs without making text blurry.

## Publication

The README documents the CI-generated artifact rather than pretending unreviewed binaries are permanent marketing assets. After a release-candidate screenshot set passes visual/privacy review, maintainers may commit selected approved PNGs under this directory and add a permanent image grid to the README.

Record the Android API/device profile and source workflow run in `what_changed.md` or the release notes when publishing a screenshot set.
