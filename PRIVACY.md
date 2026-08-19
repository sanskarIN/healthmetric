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

- a locally generated entry identifier;
- timestamp;
- calculator type;
- calculated value;
- short neutral summary.

Raw weight, height, and waist inputs are not stored in history by the current implementation.

Users can choose a maximum retention of 50, 100, 250, or 500 saved results. The default retention limit is 100. Lowering the limit immediately removes older entries beyond the selected maximum. Individual history entries can be deleted, with an immediate in-app undo action, or all history can be erased after confirmation.

## Data export and restore

The user can explicitly export local HealthMetric data as JSON in either of two ways:

- save a backup file through Android's Storage Access Framework document picker; or
- share the JSON through Android's explicit share chooser.

HealthMetric does not silently upload exported data. Once an exported copy is handed to another app, cloud provider, removable drive, or other location, that copy is governed by the destination's privacy and security behavior.

The user can restore a HealthMetric JSON backup by selecting a local document. Restore behavior is defensive:

- only backup schema version 1 is accepted;
- backup reads and writes are limited to 1 MiB;
- restored history is capped by the selected supported retention limit and never exceeds 500 entries;
- malformed individual history records are ignored rather than crashing the app;
- blank/invalid identifiers, negative timestamps, non-finite values, and unknown calculator types are rejected at record level;
- duplicate history identifiers are deduplicated;
- a backup that omits the history preference restores with history disabled;
- unsupported retention values fall back to the privacy-conscious default of 100.

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

## Children and teens

The current BMI and waist-to-height reference calculators are explicitly designed for adults age 18 or older. HealthMetric does not apply these adult references to people under 18.

## Third parties

The repository links to GitHub, email contacts, and Buy Me a Coffee from the About/support documentation. These services are not required for calculator operation and are opened only after a user action.

## Changes

Privacy-impacting behavior changes must update this document, relevant tests, `CHANGELOG.md`, and `what_changed.md` before release.

## Contact

- Support: `supportramsandesh@gmail.com`
- Business: `sanskarin@outlook.in`
- Business: `sanskarin.business@gmail.com`
