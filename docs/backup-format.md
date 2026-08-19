# Backup Format

HealthMetric uses a small versioned JSON document for explicit user-initiated backup and restore. This document defines schema version `1`.

## Goals

The format is designed to be:

- human-readable;
- bounded in size;
- explicit about version compatibility;
- recoverable when individual history records are damaged;
- unable to transfer device-local privacy consent or adult-use gate decisions.

## Size limit

HealthMetric accepts and writes backup payloads up to **1 MiB (1,048,576 bytes)** of UTF-8 JSON.

The Android document layer enforces this while reading/writing streams, and the DataStore restore boundary independently checks the raw UTF-8 byte size before JSON parsing.

## Schema version 1

A current backup has this shape:

```json
{
  "schemaVersion": 1,
  "historyRetentionLimit": 100,
  "themeMode": "SYSTEM",
  "history": [
    {
      "id": "1700000000000-123456",
      "timestampEpochMillis": 1700000000000,
      "calculator": "BMI",
      "value": 22.9,
      "summary": "Within adult reference range"
    }
  ]
}
```

Example values are fictional.

## Top-level fields

| Field | Type | Required behavior |
|---|---|---|
| `schemaVersion` | integer | Must equal `1`; unsupported versions are rejected before mutation. |
| `historyRetentionLimit` | integer | Supported values: `50`, `100`, `250`, `500`; unsupported/missing values normalize to `100`. |
| `themeMode` | string | `SYSTEM`, `LIGHT`, or `DARK`; invalid/missing values normalize to `SYSTEM`. |
| `history` | array | Malformed/missing arrays restore as an empty valid history collection. |

## History record fields

| Field | Type | Rule |
|---|---|---|
| `id` | string | Trimmed, non-blank, capped to 96 characters, unique after sanitation. |
| `timestampEpochMillis` | integer | Must be non-negative. |
| `calculator` | string | `BMI` or `WAIST_TO_HEIGHT`. |
| `value` | number | Must be finite. |
| `summary` | string | Optional on import; capped to 240 characters. |

Invalid records are skipped independently so one damaged entry does not discard valid neighboring entries. If multiple valid records normalize to the same ID, the first valid record is retained.

Restored history is capped to the normalized `historyRetentionLimit` and can never exceed the application-wide maximum of 500 records.

## Deliberately non-portable state

The following DataStore values are **not exported** and are **not changed by restore**:

- `history_enabled` — consent to save future calculation results;
- `adult_use_confirmed` — the adult-only reference eligibility choice;
- `onboarding_complete` — device-local onboarding/safety state.

Older schema-v1 documents may contain JSON fields named `historyEnabled`, `adultUseConfirmed`, or `onboardingComplete`. Current HealthMetric ignores them during restore. Their presence cannot enable future history saving or adult-only calculator access.

## Restore transaction behavior

The UI reads the chosen file into a bounded string and asks the user for confirmation. Only after confirmation does the ViewModel call the DataStore restore operation.

The restore operation validates size and top-level schema before opening the DataStore edit transaction. Portable settings and sanitized history are then written together in one DataStore edit.

Current device-local consent/safety values are left untouched.

## Forward compatibility

A future incompatible backup format must increment `schemaVersion` rather than silently changing version `1` semantics.

When adding a new schema version:

1. keep version `1` import support when practical;
2. define explicit migration behavior;
3. add deterministic migration tests;
4. retain bounded payload/history protections or document an ADR for any reviewed replacement;
5. never make consent/adult-gate state portable without a dedicated privacy and safety review;
6. update `PRIVACY.md`, `CHANGELOG.md`, `docs/release.md`, and `what_changed.md`.

## Privacy note

A HealthMetric backup contains calculated measurement history and should be treated as private data. HealthMetric does not automatically upload it. The user explicitly selects the destination file or receiving application, and copies outside HealthMetric are governed by that destination.
