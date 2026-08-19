# Evidence and Reference Metadata

HealthMetric separates calculation arithmetic from user-facing reference metadata so reference updates are explicit, reviewable, and testable.

## Adult BMI reference profile

Current profile ID: `adult-general-v1`

Source metadata embedded in the shared domain module:

- Title: Body mass index (BMI)
- Publisher: World Health Organization
- Source URL: https://www.who.int/data/gho/data/themes/topics/topic-details/GHO/body-mass-index
- Scope in HealthMetric: general adult population screening context
- Last source review recorded in code: `2026-08-19`

The source review confirms that the referenced WHO page continues to distinguish adult BMI indicators at `< 18.5`, `>= 25`, and `>= 30`, while separately listing child/adolescent indicators. HealthMetric keeps its adult-only gate and does not reuse the adult profile for people under 18.

Current reference boundaries encoded by the profile:

| BMI boundary | HealthMetric label |
|---|---|
| below 18.5 | Below adult reference range |
| 18.5 to below 25.0 | Within adult reference range |
| 25.0 to below 30.0 | Above adult reference range |
| 30.0 and above | Well above adult reference range |

These labels intentionally avoid appearance judgments. HealthMetric does not present them as diagnoses or personalized body goals.

## Adult-only scope

The current reference calculators are not applied to users who indicate they are under 18. Age-specific growth assessment requires different context and is outside the scope of this project.

## Waist-to-height ratio

HealthMetric currently calculates the mathematical waist-to-height ratio for adults and presents the value neutrally. It deliberately does **not** assign a risk tier, diagnosis, appearance grade, or target from the ratio in the initial release.

This keeps the feature useful as a measurement calculator without implying clinical interpretation that the project has not separately versioned and sourced.

## Updating evidence

A change to reference thresholds or clinical interpretation requires:

1. a new/reviewed authoritative source;
2. an updated `reviewedOnIsoDate` value for the source metadata;
3. an updated stable profile ID/version when interpretation changes;
4. boundary/regression tests;
5. neutral user-facing copy review;
6. `CHANGELOG.md` entry;
7. updates to this document and `what_changed.md`;
8. release notes that explicitly identify the reference change.

Do not silently change an existing versioned reference profile after a release if that would alter historical interpretation.
