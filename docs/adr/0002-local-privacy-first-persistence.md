# ADR 0002: Use local privacy-first persistence with optional history

- Status: Accepted
- Date: 2026-08-19

## Context

Users may benefit from reviewing recent measurements, but measurement history can be sensitive. Core calculator functionality does not require a server, account, or analytics system.

## Decision

Use AndroidX Preferences DataStore for small local settings/history in the initial Android client.

Design constraints:

- history can be disabled;
- raw weight/height/waist inputs are not stored in history;
- stored history is capped at 500 entries;
- users can erase history or all local data;
- export/restore is explicit and user-initiated;
- Android backup is disabled;
- no backend/cloud sync is required.

## Consequences

### Positive

- Core flows remain offline.
- Low permission and infrastructure footprint.
- User has direct deletion/export controls.
- No account database or remote health-data store exists.

### Trade-offs

- Cross-device synchronization is intentionally absent.
- Preferences DataStore is not appropriate for an unbounded/highly relational history dataset.

## Migration trigger

If future requirements need substantially larger/queryable history, migrate to a structured local database with explicit schema migrations and preserve privacy controls. Do not simply raise the JSON preference limit indefinitely.
