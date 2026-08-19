package io.github.sanskarin.healthmetric.desktop

import io.github.sanskarin.healthmetric.domain.BmiCalculator
import io.github.sanskarin.healthmetric.domain.ImperialBodyInput
import io.github.sanskarin.healthmetric.domain.MetricBodyInput
import io.github.sanskarin.healthmetric.domain.WaistToHeightCalculator

internal sealed interface DesktopCalculationOutcome {
    data class Success(
        val valueLabel: String,
        val contextLabel: String,
        val explanation: String,
        val notice: String,
    ) : DesktopCalculationOutcome

    data class Failure(val message: String) : DesktopCalculationOutcome
}

internal object DesktopCalculations {
    fun metricBmi(weightKg: String, heightCm: String): DesktopCalculationOutcome {
        val weight = DesktopNumbers.parseDecimal(weightKg)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid weight in kilograms.")
        val height = DesktopNumbers.parseDecimal(heightCm)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid height in centimeters.")

        return runCatching {
            BmiCalculator.calculateMetric(MetricBodyInput(weightKg = weight, heightCm = height))
        }.fold(
            onSuccess = { result ->
                DesktopCalculationOutcome.Success(
                    valueLabel = "BMI ${result.displayBmi}",
                    contextLabel = result.band.label,
                    explanation = result.band.explanation,
                    notice = result.educationalNotice,
                )
            },
            onFailure = ::failure,
        )
    }

    fun imperialBmi(weightLb: String, feet: String, inches: String): DesktopCalculationOutcome {
        val weight = DesktopNumbers.parseDecimal(weightLb)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid weight in pounds.")
        val heightFeet = DesktopNumbers.parseWholeNumber(feet)
            ?: return DesktopCalculationOutcome.Failure("Enter height feet as a whole number.")
        val heightInches = DesktopNumbers.parseDecimal(inches)
            ?: return DesktopCalculationOutcome.Failure("Enter valid remaining height inches.")
        validateImperialHeightComponents(heightFeet, heightInches)?.let { return it }

        return runCatching {
            BmiCalculator.calculateImperial(
                ImperialBodyInput(
                    weightLb = weight,
                    heightFeet = heightFeet,
                    heightInches = heightInches,
                ),
            )
        }.fold(
            onSuccess = { result ->
                DesktopCalculationOutcome.Success(
                    valueLabel = "BMI ${result.displayBmi}",
                    contextLabel = result.band.label,
                    explanation = result.band.explanation,
                    notice = result.educationalNotice,
                )
            },
            onFailure = ::failure,
        )
    }

    fun metricWaistToHeight(waistCm: String, heightCm: String): DesktopCalculationOutcome {
        val waist = DesktopNumbers.parseDecimal(waistCm)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid waist measurement in centimeters.")
        val height = DesktopNumbers.parseDecimal(heightCm)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid height in centimeters.")

        return runCatching {
            WaistToHeightCalculator.calculateMetric(waistCm = waist, heightCm = height)
        }.fold(
            onSuccess = { result ->
                DesktopCalculationOutcome.Success(
                    valueLabel = "Waist-to-height ratio ${result.displayRatio}",
                    contextLabel = "Adult educational screening value",
                    explanation = "This number is presented without appearance rankings or a personal target.",
                    notice = result.educationalNotice,
                )
            },
            onFailure = ::failure,
        )
    }

    fun imperialWaistToHeight(
        waistInches: String,
        heightFeet: String,
        heightInches: String,
    ): DesktopCalculationOutcome {
        val waist = DesktopNumbers.parseDecimal(waistInches)
            ?: return DesktopCalculationOutcome.Failure("Enter a valid waist measurement in inches.")
        val feet = DesktopNumbers.parseWholeNumber(heightFeet)
            ?: return DesktopCalculationOutcome.Failure("Enter height feet as a whole number.")
        val inches = DesktopNumbers.parseDecimal(heightInches)
            ?: return DesktopCalculationOutcome.Failure("Enter valid remaining height inches.")
        validateImperialHeightComponents(feet, inches)?.let { return it }
        val totalHeightInches = (feet * 12.0) + inches

        return runCatching {
            WaistToHeightCalculator.calculateImperial(
                waistInches = waist,
                heightInches = totalHeightInches,
            )
        }.fold(
            onSuccess = { result ->
                DesktopCalculationOutcome.Success(
                    valueLabel = "Waist-to-height ratio ${result.displayRatio}",
                    contextLabel = "Adult educational screening value",
                    explanation = "This number is presented without appearance rankings or a personal target.",
                    notice = result.educationalNotice,
                )
            },
            onFailure = ::failure,
        )
    }

    private fun validateImperialHeightComponents(
        feet: Int,
        remainingInches: Double,
    ): DesktopCalculationOutcome.Failure? = when {
        feet !in 0..8 -> DesktopCalculationOutcome.Failure("Enter height feet from 0 to 8.")
        remainingInches !in 0.0..<12.0 -> DesktopCalculationOutcome.Failure(
            "Enter remaining height inches from 0 up to, but not including, 12.",
        )
        else -> null
    }

    private fun failure(error: Throwable): DesktopCalculationOutcome.Failure =
        DesktopCalculationOutcome.Failure(error.message ?: "Unable to calculate from the entered measurements.")
}
