# ADR 0003: Version adult reference profiles and keep them separate from UI

- Status: Accepted
- Date: 2026-08-19

## Context

Adult BMI reference thresholds and explanatory source metadata may evolve. Hard-coding thresholds inside Compose screens would make evidence changes difficult to audit and test.

## Decision

Represent adult BMI reference information as a versioned `BmiReferenceProfile` in the shared domain module.

A profile contains:

- stable ID;
- display name;
- adult-only marker;
- ordered threshold bands;
- neutral explanations;
- evidence/source metadata.

UI renders the profile result but does not own the thresholds.

## Safety language

Reference bands are population screening context. They must not be presented as medical diagnoses, appearance scores, or personal body goals. The current adult references are not applied to users who indicate they are under 18.

## Consequences

### Positive

- Threshold changes are centralized and testable.
- Source metadata travels with the reference profile.
- Future profiles can coexist without silently rewriting historical meaning.

### Trade-offs

- Reference objects add a small amount of domain-model complexity.
- Evidence review dates/version migration will need explicit maintenance.

## Change procedure

Any threshold/source change requires an updated profile ID/version, boundary tests, documentation, changelog entry, and review of user-facing language.
