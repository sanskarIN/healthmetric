package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
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
    fun rejectsImperialInchesOutsideSingleFoot() {
        assertFailsWith<ValidationError.ImperialHeightOutOfRange> {
            BmiCalculator.calculateImperial(
                ImperialBodyInput(weightLb = 180.0, heightFeet = 5, heightInches = 12.0),
            )
        }
    }
}
