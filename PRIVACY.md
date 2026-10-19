# HealthMetric Privacy Policy

## Summary

HealthMetric is designed as an offline-first adult health measurement calculator. The repository does not include advertising SDKs, analytics trackers, accounts, cloud synchronization, or a required backend.

## Data processed

When an adult user chooses to calculate a measurement, the app may process:

- weight and height entered for BMI;
- waist and height entered for waist-to-height ratio;
- calculated values shown on screen.

These inputs are processed locally on the device.

## Local history

Local history is optional and **disabled by default**. A user must explicitly enable it in Settings before new calculation results are stored. When enabled, HealthMetric stores a bounded history containing:

- a locally generated collision-resistant UUID identifier for new records;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not stored in history by the current implementation.

Users can choose a maximum retention of 50, 100, 250, or 500 saved results. The default retention limit is 100. History is normalized newest-first by timestamp before the retention limit is applied. Lowering the limit immediately removes older entries beyond the selected maximum. Individual history entries can be deleted, with an immediate in-app undo action that restores the entry to chronological position, or all history can be erased after confirmation.

Imported schema-v1 identifiers are not required to be UUIDs; they remain bounded, validated, and deduplicated before reaching application history.

## Device-local consent and safety state

Three settings are intentionally **not portable** through HealthMetric backups:

- whether local history saving is currently enabled;
- whether adult use was confirmed;
- whether onboarding was completed.

A backup therefore cannot silently enable future history collection on another installation and cannot enable adult reference calculators by importing another person's confirmation state. Restore preserves the current device's consent and adult-use gate state.

## Data export and restore

The user can explicitly export local HealthMetric data as JSON in either of two ways:

- save a backup file through Android's Storage Access Framework document picker; or
- share the JSON through Android's explicit share chooser.

Portable backup content contains supported presentation/retention preferences and bounded calculation history. It deliberately omits device-local consent and adult-use gate state.

HealthMetric does not silently upload exported data. Once an exported copy is handed to another app, cloud provider, removable drive, or other location, that copy is governed by the destination's privacy and security behavior.

The user can restore a HealthMetric JSON backup by selecting a local document. After the file is read, HealthMetric asks for confirmation before replacing portable local settings/history. Restore behavior is defensive:

- only backup schema version 1 is accepted;
- backup reads and writes are limited to 1 MiB;
- malformed individual history records are ignored rather than crashing the app;
- blank/invalid identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected at record level;
- duplicate history identifiers are deduplicated;
- accepted records are sorted newest-first by timestamp before retention is applied;
- restored history is capped by the selected supported retention limit and never exceeds 500 entries;
- unsupported retention values fall back to the privacy-conscious default of 100;
- current history opt-in and adult-use/onboarding state are preserved rather than imported.

Because chronological sorting occurs before retention, arbitrary JSON array order does not decide which valid recent records survive a bounded restore.

## Deletion controls

Users can:

- disable future local history;
- delete individual history entries;
- undo an individual deletion immediately from the in-app snackbar;
- erase all saved history after confirmation;
- delete all HealthMetric local data and settings after confirmation.

Deleting all local data returns the app to first-run onboarding and restores privacy-first defaults.

## Network behavior

Core functionality does not require network access. The current Android manifest does not request the Internet permission and disallows cleartext traffic. External links in the About and Settings screens are opened only when the user explicitly selects them.

## Android backups

Android application backup is disabled (`android:allowBackup="false"`) to reduce unintended replication of locally stored measurements. The only supported HealthMetric backup/export paths are explicit user actions in Settings.

## Logging

HealthMetric uses a small structured logger for operational events such as deletion, retention changes, and export/restore/link failures. It accepts fixed event names and a sanitized exception type only. It does not accept raw measurements, backup contents, email addresses, tokens, credentials, or arbitrary user-provided text.

## Release screenshot evidence

The Android instrumentation workflow can capture a fixed release-evidence screenshot set from the real app on an emulator. The automated journey uses fictional/example measurements only and writes the images to app-scoped external storage before GitHub Actions uploads them as a workflow artifact.

Release screenshots are not automatic telemetry and are not generated or uploaded during normal user operation. The capture code runs only as part of the repository's Android instrumentation test suite. Any screenshot selected for permanent publication must receive human visual/privacy review first.

## Children and teens

The current BMI and waist-to-height reference calculators are explicitly designed for adults age 18 or older. HealthMetric does not apply these adult references to people under 18. Adult-use confirmation is never exported or imported through backups.

## Third parties

The repository links to GitHub, email contacts, and Buy Me a Coffee from the About/support documentation. These services are not required for calculator operation and are opened only after a user action.

## Changes

Privacy-impacting behavior changes must update this document, relevant tests, `CHANGELOG.md`, and `what_changed.md` before release.

## Contact

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
