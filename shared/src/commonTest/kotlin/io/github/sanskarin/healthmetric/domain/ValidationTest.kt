package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidationTest {
    @Test
    fun rejectsNonFiniteWeight() {
        assertFailsWith<ValidationError.NonFiniteNumber> {
            BmiCalculator.calculateMetric(
                MetricBodyInput(weightKg = Double.NaN, heightCm = 170.0),
            )
        }
    }

    @Test
    fun rejectsImplausibleAdultHeight() {
        assertFailsWith<ValidationError.HeightOutOfRange> {
            BmiCalculator.calculateMetric(
                MetricBodyInput(weightKg = 70.0, heightCm = 90.0),
            )
        }
    }

    @Test
    fun reportsMetricWeightRangeInKilograms() {
        val error = assertFailsWith<ValidationError.WeightOutOfRange> {
            BmiCalculator.calculateMetric(
                MetricBodyInput(weightKg = 10.0, heightCm = 170.0),
            )
        }

        assertEquals(
            "Weight must be between 20 kg and 500 kg for this adult educational calculator.",
            error.message,
        )
    }

    @Test
    fun reportsImperialWeightRangeInPounds() {
        val error = assertFailsWith<ValidationError.ImperialWeightOutOfRange> {
            BmiCalculator.calculateImperial(
                ImperialBodyInput(weightLb = 20.0, heightFeet = 5, heightInches = 8.0),
            )
        }

        assertEquals(
            "Weight must be between 44 lb and 1102.5 lb for this adult educational calculator.",
            error.message,
        )
    }

    @Test
    fun rejectsImperialInchesOutsideSingleFoot() {
        assertFailsWith<ValidationError.ImperialHeightOutOfRange> {
            BmiCalculator.calculateImperial(
                ImperialBodyInput(weightLb = 180.0, heightFeet = 5, heightInches = 12.0),
            )
        }
    }
}
