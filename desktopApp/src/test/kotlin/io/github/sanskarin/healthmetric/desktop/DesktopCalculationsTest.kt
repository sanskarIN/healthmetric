package io.github.sanskarin.healthmetric.desktop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DesktopCalculationsTest {
    @Test
    fun metricBmiUsesSharedAdultReference() {
        val outcome = DesktopCalculations.metricBmi(weightKg = "70", heightCm = "175")
        val success = assertIs<DesktopCalculationOutcome.Success>(outcome)

        assertEquals("BMI 22.9", success.valueLabel)
        assertEquals("Within adult reference range", success.contextLabel)
        assertTrue(success.notice.startsWith("For adults only."))
    }

    @Test
    fun imperialBmiAcceptsFeetAndRemainingInches() {
        val outcome =
            DesktopCalculations.imperialBmi(
                weightLb = "154.324",
                feet = "5",
                inches = "8.8976",
            )
        val success = assertIs<DesktopCalculationOutcome.Success>(outcome)

        assertEquals("BMI 22.9", success.valueLabel)
    }

    @Test
    fun imperialBmiRejectsTwelveRemainingInches() {
        val outcome =
            DesktopCalculations.imperialBmi(
                weightLb = "180",
                feet = "5",
                inches = "12",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals(
            "Enter remaining height inches from 0 up to, but not including, 12.",
            failure.message,
        )
    }

    @Test
    fun imperialBmiRejectsFeetOutsideComponentRange() {
        val outcome =
            DesktopCalculations.imperialBmi(
                weightLb = "180",
                feet = "9",
                inches = "0",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals("Enter height feet from 0 to 8.", failure.message)
    }

    @Test
    fun imperialBmiReportsWeightRangeInPounds() {
        val outcome =
            DesktopCalculations.imperialBmi(
                weightLb = "20",
                feet = "5",
                inches = "8",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals(
            "Weight must be between 44 lb and 1102.5 lb for this adult educational calculator.",
            failure.message,
        )
    }

    @Test
    fun metricRatioReturnsNeutralContext() {
        val outcome = DesktopCalculations.metricWaistToHeight(waistCm = "80", heightCm = "175")
        val success = assertIs<DesktopCalculationOutcome.Success>(outcome)

        assertEquals("Waist-to-height ratio 0.46", success.valueLabel)
        assertEquals("Adult educational screening value", success.contextLabel)
        assertTrue(success.explanation.contains("without appearance rankings"))
    }

    @Test
    fun imperialRatioRejectsTwelveRemainingInches() {
        val outcome =
            DesktopCalculations.imperialWaistToHeight(
                waistInches = "32",
                heightFeet = "5",
                heightInches = "12",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals(
            "Enter remaining height inches from 0 up to, but not including, 12.",
            failure.message,
        )
    }

    @Test
    fun imperialRatioRejectsFeetOutsideComponentRange() {
        val outcome =
            DesktopCalculations.imperialWaistToHeight(
                waistInches = "32",
                heightFeet = "9",
                heightInches = "0",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals("Enter height feet from 0 to 8.", failure.message)
    }

    @Test
    fun imperialRatioReportsWaistRangeInInches() {
        val outcome =
            DesktopCalculations.imperialWaistToHeight(
                waistInches = "5",
                heightFeet = "5",
                heightInches = "8",
            )
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals(
            "Waist measurement must be between about 11.81 in and 98.43 in for this adult educational calculator.",
            failure.message,
        )
    }

    @Test
    fun invalidTextReturnsFieldSpecificFailure() {
        val outcome = DesktopCalculations.metricBmi(weightKg = "abc", heightCm = "175")
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertEquals("Enter a valid weight in kilograms.", failure.message)
    }

    @Test
    fun sharedRangeValidationIsPreserved() {
        val outcome = DesktopCalculations.metricBmi(weightKg = "10", heightCm = "175")
        val failure = assertIs<DesktopCalculationOutcome.Failure>(outcome)

        assertTrue(failure.message.contains("adult educational calculator"))
    }
}
