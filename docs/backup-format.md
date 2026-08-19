# Backup Format

HealthMetric uses a small versioned JSON document for explicit user-initiated Android backup and restore. This document defines schema version `1`.

The desktop client intentionally has no HealthMetric persistence/backup layer; its measurement/session state is ephemeral. See [`desktop.md`](desktop.md) and ADR 0005.

## Goals

The Android format is designed to be:

- human-readable;
- bounded in size;
- explicit about version compatibility;
- recoverable when individual history records are damaged;
- fail-closed when the required top-level history collection is missing, has the wrong JSON type, or is non-empty but contains no valid records;
- deterministically ordered after restore;
- unable to transfer device-local privacy consent or adult-use gate decisions.

## Size and encoding limit

HealthMetric accepts and writes Android backup payloads up to **1 MiB (1,048,576 bytes)** of well-formed UTF-8 JSON.

The Android document layer enforces the byte limit while reading/writing streams, and the DataStore restore boundary independently checks the raw UTF-8 byte size before JSON parsing. Malformed UTF-8 byte sequences are rejected at the document-read boundary rather than being silently replaced with Unicode replacement characters.

## Schema version 1

A current backup has this shape:

```json
{
  "schemaVersion": 1,
  "historyRetentionLimit": 100,
  "themeMode": "SYSTEM",
  "history": [
    {
      "id": "9c2058d8-0bda-4703-8e84-e9c4b8971228",
      "timestampEpochMillis": 1700000000000,
      "calculator": "BMI",
      "value": 22.9,
      "summary": "Example adult reference summary"
    }
  ]
}
```

Example values are fictional. New locally recorded Android entries use UUID identifiers; imported schema-v1 IDs are not required to be UUIDs as long as they satisfy the validation rules below.

## Top-level fields

| Field | Type | Required behavior |
|---|---|---|
| `schemaVersion` | integer | Must equal `1`; unsupported versions are rejected before mutation. |
| `historyRetentionLimit` | integer | Supported values: `50`, `100`, `250`, `500`; unsupported/missing values normalize to `100`. |
| `themeMode` | string | `SYSTEM`, `LIGHT`, or `DARK`; invalid/missing values normalize to `SYSTEM`. |
| `history` | array | Required. A missing field or non-array value rejects the backup before any local DataStore mutation. An empty array is valid. A non-empty array must contain at least one valid history record after sanitation. |

The top-level `history` container is intentionally stricter than the records inside it. Treating a missing or wrong-type container as a valid empty history could silently erase the user's existing local history after a restore confirmation. HealthMetric therefore rejects that document instead.

An explicitly empty `history: []` is a valid request to restore a backup with no portable history. By contrast, a non-empty array from which every record is invalid is rejected before mutation rather than being interpreted as an intentional empty-history restore.

## History record fields

| Field | Type | Rule |
|---|---|---|
| `id` | string | Trimmed, non-blank, capped to 96 characters, unique after sanitation. |
| `timestampEpochMillis` | integer | Must be non-negative. |
| `calculator` | string | `BMI` or `WAIST_TO_HEIGHT`. |
| `value` | number | Must be finite. |
| `summary` | string | Optional on import; capped to 240 characters. |

Invalid records inside a valid `history` array are skipped independently so one damaged entry does not discard valid neighboring entries. If multiple valid records normalize to the same ID, the first valid record in the input document is retained. At least one valid record must survive when the input array itself is non-empty.

After validation/deduplication, accepted records are sorted by `timestampEpochMillis` descending (newest first). The normalized `historyRetentionLimit` is applied after that sort, and restored history can never exceed the application-wide maximum of 500 records. This means arbitrary JSON array order cannot decide which chronologically newest records survive the cap.

The same newest-first invariant is applied when Android records a result or restores a deleted entry through Undo.

## Deliberately non-portable state

The following Android DataStore values are **not exported** and are **not changed by restore**:

- `history_enabled` — consent to save future calculation results;
- `adult_use_confirmed` — the adult-only reference eligibility choice;
- `onboarding_complete` — device-local onboarding/safety state.

Older schema-v1 documents may contain JSON fields named `historyEnabled`, `adultUseConfirmed`, or `onboardingComplete`. Current HealthMetric ignores them during restore. Their presence cannot enable future history saving or adult-only calculator access.

## Restore transaction behavior

The Android UI reads the chosen file into a bounded string and asks the user for confirmation. Only after confirmation does the ViewModel call the DataStore restore operation.

Before opening the DataStore edit transaction, the restore path validates:

1. that the selected document is bounded and well-formed UTF-8;
2. the UTF-8 payload size at the DataStore boundary;
3. parseable top-level JSON;
4. supported `schemaVersion`;
5. presence and array type of the required `history` field;
6. portable setting normalization;
7. individual history records, deduplication, chronology, and retention bounds;
8. that a non-empty history array retains at least one valid record after sanitation.

Only after those preconditions are resolved are portable settings and sanitized, deduplicated, newest-first bounded history written together in one DataStore edit. A malformed UTF-8 document, missing/non-array `history` field, or non-empty all-invalid history array therefore cannot clear existing local data or change portable preferences.

Current device-local consent/safety values are left untouched.

## Forward compatibility

A future incompatible Android backup format must increment `schemaVersion` rather than silently changing version `1` semantics.

When adding a new schema version:

1. keep version `1` import support when practical;
2. define explicit migration behavior;
3. add deterministic migration tests;
4. retain bounded payload/history protections or document an ADR for any reviewed replacement;
5. retain strict UTF-8 decoding at the document boundary;
6. retain required top-level structural validation before mutation;
7. retain the distinction between intentional empty history and non-empty all-invalid history unless a reviewed migration explicitly changes it;
8. retain deterministic history ordering or explicitly document a reviewed alternative;
9. never make consent/adult-gate state portable without a dedicated privacy and safety review;
10. update `PRIVACY.md`, `CHANGELOG.md`, `docs/release.md`, and `what_changed.md`.

## Privacy note

A HealthMetric Android backup contains calculated measurement history and should be treated as private data. HealthMetric does not automatically upload it. The user explicitly selects the destination file or receiving application, and copies outside HealthMetric are governed by that destination.

Desktop currently creates no equivalent backup because the desktop client deliberately does not persist measurement/session data.
