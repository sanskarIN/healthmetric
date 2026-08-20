package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HealthMetricEngineTest {
    @Test
    fun `adult metric BMI uses the shared reference profile`() {
        val summary = HealthMetricEngine.calculateAdultMetricBmi(
            ageYears = 18,
            weightKg = 70.0,
            heightCm = 175.0,
        )

        assertEquals(22.9, summary.displayValue)
        assertEquals("Within adult reference range", summary.referenceLabel)
        assertTrue(summary.educationalNotice.contains("For adults only"))
    }

    @Test
    fun `under eighteen requests are rejected before calculation`() {
        assertFailsWith<AdultOnlyUsageError> {
            HealthMetricEngine.calculateAdultMetricBmi(
                ageYears = 17,
                weightKg = 70.0,
                heightCm = 175.0,
            )
        }
    }

    @Test
    fun `adult waist to height result delegates to shared calculator`() {
        val summary = HealthMetricEngine.calculateAdultMetricWaistToHeight(
            ageYears = 21,
            waistCm = 80.0,
            heightCm = 180.0,
        )

        assertEquals(0.44, summary.displayValue)
        assertTrue(summary.educationalNotice.contains("screening"))
    }

    @Test
    fun `eligibility helper has an explicit adult boundary`() {
        assertTrue(HealthMetricEngine.isAdultAgeEligible(18))
        assertEquals(false, HealthMetricEngine.isAdultAgeEligible(17))
    }
}
