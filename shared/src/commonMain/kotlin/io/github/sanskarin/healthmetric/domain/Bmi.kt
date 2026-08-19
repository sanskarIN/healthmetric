package io.github.sanskarin.healthmetric.domain

import kotlin.math.pow
import kotlin.math.round

data class EvidenceSource(
    val title: String,
    val publisher: String,
    val url: String,
    val note: String,
)

data class BmiReferenceBand(
    val minInclusive: Double?,
    val maxExclusive: Double?,
    val label: String,
    val explanation: String,
)

data class BmiReferenceProfile(
    val id: String,
    val displayName: String,
    val adultOnly: Boolean,
    val bands: List<BmiReferenceBand>,
    val source: EvidenceSource,
) {
    fun bandFor(bmi: Double): BmiReferenceBand = bands.first { band ->
        val aboveMinimum = band.minInclusive?.let { bmi >= it } ?: true
        val belowMaximum = band.maxExclusive?.let { bmi < it } ?: true
        aboveMinimum && belowMaximum
    }

    companion object {
        val AdultGeneralReference = BmiReferenceProfile(
            id = "adult-general-v1",
            displayName = "General adult BMI reference",
            adultOnly = true,
            bands = listOf(
                BmiReferenceBand(
                    minInclusive = null,
                    maxExclusive = 18.5,
                    label = "Below adult reference range",
                    explanation = "This result is below the commonly used adult population reference band. BMI is only a screening measure and does not describe overall health on its own.",
                ),
                BmiReferenceBand(
                    minInclusive = 18.5,
                    maxExclusive = 25.0,
                    label = "Within adult reference range",
                    explanation = "This result is within a commonly used adult population reference band. It is not a diagnosis or a personal appearance target.",
                ),
                BmiReferenceBand(
                    minInclusive = 25.0,
                    maxExclusive = 30.0,
                    label = "Above adult reference range",
                    explanation = "This result is above a commonly used adult population reference band. Individual health context can differ substantially from BMI alone.",
                ),
                BmiReferenceBand(
                    minInclusive = 30.0,
                    maxExclusive = null,
                    label = "Well above adult reference range",
                    explanation = "This result is well above a commonly used adult population reference band. BMI alone cannot determine a person's health status.",
                ),
            ),
            source = EvidenceSource(
                title = "Body mass index (BMI)",
                publisher = "World Health Organization",
                url = "https://www.who.int/data/gho/data/themes/topics/topic-details/GHO/body-mass-index",
                note = "Population-level adult BMI reference information. HealthMetric presents this for education, not diagnosis.",
            ),
        )
    }
}

data class BmiResult(
    val bmi: Double,
    val displayBmi: Double,
    val band: BmiReferenceBand,
    val reference: BmiReferenceProfile,
    val educationalNotice: String = EDUCATIONAL_NOTICE,
) {
    companion object {
        const val EDUCATIONAL_NOTICE: String =
            "For adults only. BMI is a population screening measure, not a diagnosis or an appearance goal. If you have health concerns, discuss them with a qualified healthcare professional."
    }
}

object BmiCalculator {
    fun calculateMetric(
        input: MetricBodyInput,
        reference: BmiReferenceProfile = BmiReferenceProfile.AdultGeneralReference,
    ): BmiResult {
        InputValidator.requireMetricBodyInput(input)
        val heightMeters = input.heightCm / 100.0
        val bmi = input.weightKg / heightMeters.pow(2)
        return resultFor(bmi, reference)
    }

    fun calculateImperial(
        input: ImperialBodyInput,
        reference: BmiReferenceProfile = BmiReferenceProfile.AdultGeneralReference,
    ): BmiResult {
        InputValidator.requireImperialBodyInput(input)
        val kilograms = UnitConverter.poundsToKilograms(input.weightLb)
        val centimeters = UnitConverter.imperialHeightToCentimeters(input.heightFeet, input.heightInches)
        return calculateMetric(MetricBodyInput(kilograms, centimeters), reference)
    }

    private fun resultFor(bmi: Double, reference: BmiReferenceProfile): BmiResult {
        val display = round(bmi * 10.0) / 10.0
        return BmiResult(
            bmi = bmi,
            displayBmi = display,
            band = reference.bandFor(bmi),
            reference = reference,
        )
    }
}
