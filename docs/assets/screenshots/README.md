# Screenshot Capture Checklist

Real screenshots must be captured from a built Android release candidate. Do not fabricate screenshots or commit images containing real personal health information.

Required capture set:

1. `01-onboarding.png` — adult-use and privacy notice.
2. `02-bmi-metric.png` — metric calculator using fictional/example measurements.
3. `03-bmi-result.png` — neutral educational BMI result and reference source.
4. `04-waist-ratio.png` — waist-to-height calculator using fictional/example measurements.
5. `05-history.png` — local history and accessible neutral chart.
6. `06-settings.png` — privacy/data and appearance controls.
7. `07-about.png` — version, MIT license, support, GitHub, BMC, and Made by the Sanskar credit.
8. `08-dark-theme.png` — representative dark-theme screen.

## Capture requirements

- Use a release/debug build from the repository, not a mockup.
- Use fictional/example data only.
- Capture at a common phone size; add tablet capture if responsive layout differs materially.
- Verify system font scaling is default for marketing captures, then separately perform large-text accessibility review.
- Do not include device notifications, email addresses other than project contacts, account names, or other personal data.
- Optimize PNGs without making text blurry.

After adding screenshots, replace the README placeholder section with the real image grid and document the device/API used.
