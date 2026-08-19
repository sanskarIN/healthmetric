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

## Data export and restore

The user can explicitly export local HealthMetric data as JSON through Android's share flow. Once exported to another app or location, that copy is governed by the destination's privacy/security behavior.

The user can restore a HealthMetric JSON backup. Restore accepts only the supported schema and caps imported history size. A backup that omits the history preference restores with history disabled.

## Deletion controls

Users can:

- disable future local history;
- erase saved history;
- delete all HealthMetric local data and settings.

Deleting all local data returns the app to first-run onboarding and restores privacy-first defaults.

## Network behavior

Core functionality does not require network access. The current Android manifest does not request the Internet permission and disallows cleartext traffic. External links in the About and Settings screens are opened only when the user explicitly selects them.

## Backups

Android application backup is disabled (`android:allowBackup="false"`) to reduce unintended replication of locally stored measurements.

## Logging

HealthMetric uses a small structured logger for operational events such as deletion or export/restore/link failures. It accepts fixed event names and a sanitized exception type only. It does not accept raw measurements, backup contents, email addresses, tokens, credentials, or arbitrary user-provided text.

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
