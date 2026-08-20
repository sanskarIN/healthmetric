package io.github.sanskarin.healthmetric.domain

class AdultOnlyUsageError : IllegalArgumentException(
    "HealthMetric reference calculators are intended for adults age 18 or older.",
)

data class BmiSummary(
    val displayValue: Double,
    val referenceLabel: String,
    val explanation: String,
    val educationalNotice: String,
)

data class WaistToHeightSummary(
    val displayValue: Double,
    val educationalNotice: String,
)

/**
 * Stable, primitive-input facade intended for platform clients.
 *
 * Keeping age eligibility and calculation routing here prevents Android, desktop,
 * web, and Apple clients from drifting into different validation behavior.
 */
object HealthMetricEngine {
    const val MINIMUM_SUPPORTED_AGE_YEARS: Int = 18

    fun calculateAdultMetricBmi(
        ageYears: Int,
        weightKg: Double,
        heightCm: Double,
    ): BmiSummary {
        requireAdult(ageYears)
        return BmiCalculator.calculateMetric(
            MetricBodyInput(
                weightKg = weightKg,
                heightCm = heightCm,
            ),
        ).toSummary()
    }

    fun calculateAdultImperialBmi(
        ageYears: Int,
        weightLb: Double,
        heightFeet: Int,
        heightInches: Double,
    ): BmiSummary {
        requireAdult(ageYears)
        return BmiCalculator.calculateImperial(
            ImperialBodyInput(
                weightLb = weightLb,
                heightFeet = heightFeet,
                heightInches = heightInches,
            ),
        ).toSummary()
    }

    fun calculateAdultMetricWaistToHeight(
        ageYears: Int,
        waistCm: Double,
        heightCm: Double,
    ): WaistToHeightSummary {
        requireAdult(ageYears)
        return WaistToHeightCalculator.calculateMetric(
            waistCm = waistCm,
            heightCm = heightCm,
        ).toSummary()
    }

    fun calculateAdultImperialWaistToHeight(
        ageYears: Int,
        waistInches: Double,
        heightInches: Double,
    ): WaistToHeightSummary {
        requireAdult(ageYears)
        return WaistToHeightCalculator.calculateImperial(
            waistInches = waistInches,
            heightInches = heightInches,
        ).toSummary()
    }

    fun isAdultAgeEligible(ageYears: Int): Boolean = ageYears >= MINIMUM_SUPPORTED_AGE_YEARS

    private fun requireAdult(ageYears: Int) {
        if (!isAdultAgeEligible(ageYears)) throw AdultOnlyUsageError()
    }
}

private fun BmiResult.toSummary(): BmiSummary = BmiSummary(
    displayValue = displayBmi,
    referenceLabel = band.label,
    explanation = band.explanation,
    educationalNotice = educationalNotice,
)

private fun WaistToHeightResult.toSummary(): WaistToHeightSummary = WaistToHeightSummary(
    displayValue = displayRatio,
    educationalNotice = educationalNotice,
)
