# HealthMetric Privacy Policy

## Summary

HealthMetric is designed as an offline-first adult health measurement calculator. The repository does not include advertising SDKs, analytics trackers, accounts, cloud synchronization, or a required backend.

The Android and desktop clients intentionally use different local-data models:

- Android can store an optional, bounded calculation history only after explicit opt-in.
- The desktop client does not persist measurement inputs, results, adult-use choice, theme selection, or navigation state. Those values exist only in application memory and are discarded when the desktop process closes.

## Data processed

When an adult user chooses to calculate a measurement, a client may process locally:

- weight and height entered for BMI;
- waist and height entered for waist-to-height ratio;
- calculated values shown on screen.

These inputs are processed on the user's device. Core calculations do not require an account or backend.

## Android local history

Android local history is optional and **disabled by default**. A user must explicitly enable it in Settings before new calculation results are stored. When enabled, HealthMetric stores a bounded history containing:

- a locally generated entry identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not stored in Android history by the current implementation.

Users can choose a maximum retention of 50, 100, 250, or 500 saved results. The default retention limit is 100. Lowering the limit immediately removes older entries beyond the selected maximum. Individual history entries can be deleted, with an immediate in-app undo action, or all history can be erased after confirmation.

## Android device-local consent and safety state

Three settings are intentionally **not portable** through HealthMetric Android backups:

- whether local history saving is currently enabled;
- whether adult use was confirmed;
- whether onboarding was completed.

A backup therefore cannot silently enable future history collection on another installation and cannot enable adult reference calculators by importing another person's confirmation state. Restore preserves the current Android installation's consent and adult-use gate state.

## Android data export and restore

The Android user can explicitly export local HealthMetric data as JSON in either of two ways:

- save a backup file through Android's Storage Access Framework document picker; or
- share the JSON through Android's explicit share chooser.

Portable backup content contains supported presentation/retention preferences and bounded calculation history. It deliberately omits device-local consent and adult-use gate state.

HealthMetric does not silently upload exported data. Once an exported copy is handed to another app, cloud provider, removable drive, or other location, that copy is governed by the destination's privacy and security behavior.

The user can restore a HealthMetric JSON backup by selecting a local document. After the file is read, HealthMetric asks for confirmation before replacing portable local settings/history. Restore behavior is defensive:

- only backup schema version 1 is accepted;
- backup reads and writes are limited to 1 MiB;
- restored history is capped by the selected supported retention limit and never exceeds 500 entries;
- malformed individual history records are ignored rather than crashing the app;
- blank/invalid identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected at record level;
- duplicate history identifiers are deduplicated;
- unsupported retention values fall back to the default of 100;
- current history opt-in and adult-use/onboarding state are preserved rather than imported.

## Android deletion controls

Android users can:

- disable future local history;
- delete individual history entries;
- undo an individual deletion immediately from the in-app snackbar;
- erase all saved history after confirmation;
- delete all HealthMetric local data and settings after confirmation.

Deleting all Android local data returns the app to first-run onboarding and restores privacy-first defaults.

## Desktop data behavior

The desktop client deliberately has no HealthMetric persistence layer.

It does not persist:

- weight, height, or waist entries;
- BMI or waist-to-height results;
- the adult-use selection;
- light/dark theme selection;
- selected calculator/navigation state.

These values are kept in Compose/JVM process memory only. Closing the desktop application discards them.

The desktop client does not import Android backup files, export a desktop backup, synchronize measurements, or require an account. This design is recorded in [`docs/adr/0005-ephemeral-desktop-client.md`](docs/adr/0005-ephemeral-desktop-client.md).

## Network and external-link behavior

Core calculation functionality does not require network access.

The current Android manifest does not request the Internet permission and disallows cleartext traffic. Android external links in About and Settings are opened only when the user explicitly selects them.

The desktop calculation core likewise does not need network access. The desktop About & evidence view can open evidence, repository, and funding URLs only after the user explicitly presses the corresponding button. Those destinations are outside HealthMetric and are governed by their own privacy/security practices.

## Android application backups

Android application backup is disabled (`android:allowBackup="false"`) to reduce unintended replication of locally stored measurements. The only supported Android HealthMetric backup/export paths are explicit user actions in Settings.

This Android backup policy does not apply to the desktop client because the desktop client does not currently persist HealthMetric measurement data.

## Logging

The Android client uses a small structured logger for operational events such as deletion, retention changes, and export/restore/link failures. It accepts fixed event names and a sanitized exception type only. It does not accept raw measurements, backup contents, email addresses, tokens, credentials, or arbitrary user-provided text.

The desktop client does not add measurement logging or a measurement persistence subsystem.

## Children and teens

The current BMI and waist-to-height reference calculators are explicitly designed for adults age 18 or older. HealthMetric does not apply these adult references to people under 18.

- Android adult-use confirmation is device-local and is never exported or imported through backups.
- Desktop adult-use selection is session-only and is not persisted.

Both clients provide an under-18 path that does not expose adult reference calculator results.

## Third parties

The repository and clients may link to GitHub, project contact channels, evidence sources, and Buy Me a Coffee. These services are not required for calculator operation and are opened only after explicit user action.

## Changes

Privacy-impacting behavior changes must update this document, relevant tests, architecture/ADR documentation where appropriate, `CHANGELOG.md`, and `what_changed.md` before release.

## Contact

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
