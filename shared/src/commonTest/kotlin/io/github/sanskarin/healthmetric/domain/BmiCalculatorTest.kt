package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BmiCalculatorTest {
    @Test
    fun metricCalculationProducesExpectedValue() {
        val result = BmiCalculator.calculateMetric(
            MetricBodyInput(weightKg = 70.0, heightCm = 175.0),
        )

        assertEquals(22.9, result.displayBmi)
        assertEquals("Within adult reference range", result.band.label)
        assertTrue(result.educationalNotice.contains("not a diagnosis"))
    }

    @Test
    fun imperialCalculationMatchesMetricEquivalent() {
        val metric = BmiCalculator.calculateMetric(
            MetricBodyInput(weightKg = 81.6466266, heightCm = 182.88),
        )
        val imperial = BmiCalculator.calculateImperial(
            ImperialBodyInput(weightLb = 180.0, heightFeet = 6, heightInches = 0.0),
        )

        assertEquals(metric.displayBmi, imperial.displayBmi)
    }

    @Test
    fun referenceBoundaryAtTwentyFiveUsesAboveReferenceBand() {
        val result = BmiReferenceProfile.AdultGeneralReference.bandFor(25.0)
        assertEquals("Above adult reference range", result.label)
    }

    @Test
    fun adultReferenceCarriesAuditableSourceMetadata() {
        val profile = BmiReferenceProfile.AdultGeneralReference

        assertEquals("adult-general-v1", profile.id)
        assertTrue(profile.adultOnly)
        assertEquals("World Health Organization", profile.source.publisher)
        assertEquals("2026-08-19", profile.source.reviewedOnIsoDate)
        assertTrue(profile.source.url.startsWith("https://"))
    }
}
