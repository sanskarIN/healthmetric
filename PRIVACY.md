# HealthMetric Privacy Policy

## Summary

HealthMetric is designed as a privacy-first adult health measurement toolkit. The repository does not include advertising SDKs, analytics trackers, accounts, cloud synchronization, or a required HealthMetric backend.

Core calculations run locally in the active client. Platform data behavior differs intentionally:

- Android includes optional bounded local history that is **disabled by default**;
- desktop, web, and iOS beta clients currently keep calculator form values/results in transient UI state and do not persist history.

## Data processed during a calculation

When an adult user chooses to calculate a measurement, a client may process:

- weight and height entered for BMI;
- waist and height entered for waist-to-height ratio;
- age in years for adult-use eligibility in the reusable cross-platform client;
- calculated values shown on screen.

The shared calculation engine processes these values locally. It does not send them to a HealthMetric server.

## Adult-use eligibility

The current BMI and waist-to-height reference calculators are intended for adults age 18 or older.

- Android uses its local onboarding/adult-use confirmation state.
- Desktop, web, and iOS use the reusable age gate in `sharedUI`.
- `HealthMetricEngine` repeats the 18+ check at the shared domain boundary.

HealthMetric does not require a date of birth, identity document, or account to perform this local eligibility check. Desktop/web/iOS do not persist the entered age in the current beta implementation.

## Android local history

Android local history is optional and **disabled by default**. A user must explicitly enable it in Settings before new calculation results are stored.

When enabled, Android stores a bounded history containing:

- locally generated entry identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not stored in Android history by the current implementation.

Users can choose a maximum retention of 50, 100, 250, or 500 saved results. Default retention is 100. Lowering the limit immediately removes older entries beyond the selected maximum. Individual entries can be deleted with immediate in-app undo, or all history can be erased after confirmation.

## Desktop, web, and iOS beta clients

The reusable `sharedUI` calculator currently has no persistence/history implementation.

In these clients:

- age and measurement form values are transient UI state;
- calculation results are transient UI state;
- no HealthMetric history database/file/browser-storage layer is created by this implementation;
- no HealthMetric analytics/advertising SDK is added;
- no cloud synchronization is performed.

If persistence is introduced later on any platform, it must receive an explicit privacy design, bounded retention/deletion controls, documentation, and tests before release.

## Android device-local consent and safety state

Three Android settings are intentionally **not portable** through HealthMetric backups:

- whether local history saving is currently enabled;
- whether adult use was confirmed;
- whether onboarding was completed.

A backup therefore cannot silently enable future history collection on another installation and cannot enable adult reference calculators by importing another person's confirmation state. Restore preserves the current Android installation's consent and adult-use gate state.

## Android data export and restore

Android users can explicitly export local HealthMetric data as JSON in either of two ways:

- save a backup file through Android's Storage Access Framework document picker; or
- share JSON through Android's explicit share chooser.

Portable backup content contains supported presentation/retention preferences and bounded calculation history. It deliberately omits device-local consent and adult-use gate state.

HealthMetric does not silently upload exported data. Once an exported copy is handed to another app, cloud provider, removable drive, or other location, that copy is governed by the destination's privacy and security behavior.

Android restore is defensive:

- only backup schema version 1 is accepted;
- backup reads and writes are limited to **1 MiB**;
- restore asks for confirmation before replacing portable local settings/history;
- restored history is capped by the selected supported retention limit and never exceeds 500 entries;
- malformed individual history records are ignored rather than crashing the app;
- blank/invalid identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected at record level;
- duplicate history identifiers are deduplicated;
- unsupported retention values fall back to 100;
- current history opt-in and adult-use/onboarding state are preserved rather than imported.

## Android deletion controls

Android users can:

- disable future local history;
- delete individual history entries;
- undo an individual deletion immediately from the in-app snackbar;
- erase all saved history after confirmation;
- delete all HealthMetric local data/settings after confirmation.

Deleting all local Android data returns the app to first-run onboarding and privacy-first defaults.

## Network behavior

### Android

Core functionality does not require network access. The current Android manifest:

- does not request the Internet permission;
- sets `android:usesCleartextTraffic="false"`;
- sets `android:allowBackup="false"`.

External GitHub, release, support, and funding links are opened only after explicit user interaction.

### Desktop and iOS

The current clients add no HealthMetric backend, telemetry, advertising, or cloud synchronization. Network access is not required for calculation logic.

### Web

The web application must naturally be delivered to a browser by whichever host/distributor serves its static assets. The calculator itself does not require a HealthMetric server API and the repository adds no analytics/advertising SDK.

A production web deployment must document its chosen hosting provider, HTTP/security headers, access logs, CDN behavior, and any other host-level processing separately from this application-code privacy policy.

## Android application backup

Android application backup is disabled (`android:allowBackup="false"`) to reduce unintended replication of locally stored calculation history. Supported HealthMetric backup/export paths are explicit user actions in Settings.

## Logging

The Android client uses a small structured logger for operational events such as deletion, retention changes, and export/restore/link failures. It accepts fixed event names and a sanitized exception type only. It does not accept raw measurements, backup contents, email addresses, tokens, credentials, or arbitrary user-provided text.

The reusable desktop/web/iOS beta calculator UI does not introduce a HealthMetric telemetry logger.

## Children and teens

HealthMetric does not apply its adult BMI/waist reference calculators to people under 18.

- Android adult-use confirmation is never exported/imported through backups.
- Desktop/web/iOS age values below 18 do not enter adult calculator content.
- The shared calculation façade rejects requests below the adult eligibility boundary.

## Third parties

Repository documentation and the Android About/support experience may link to GitHub, email contacts, and Buy Me a Coffee. These services are not required for calculator operation and are reached only after user action.

Build infrastructure uses GitHub Actions and normal platform build ecosystems. Development/build-time service behavior is separate from runtime calculator data processing.

## Future privacy changes

Privacy-impacting changes must update, as applicable:

- this policy;
- tests and repository invariants;
- relevant ADR/architecture documentation;
- `CHANGELOG.md`;
- `ROADMAP.md`;
- `what_changed.md`.

New cross-platform persistence is not allowed to appear silently; it must define opt-in/default behavior, retention, deletion, export/restore scope, and platform threat model first.

## Contact

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
