package io.github.sanskarin.healthmetric.domain

import kotlin.math.round

data class WaistToHeightResult(
    val ratio: Double,
    val displayRatio: Double,
    val educationalNotice: String =
        "For adults only. Waist-to-height ratio is a simple screening measurement and cannot diagnose health conditions or define an appearance goal.",
)

object WaistToHeightCalculator {
    fun calculateMetric(waistCm: Double, heightCm: Double): WaistToHeightResult {
        InputValidator.requireWaistAndHeight(waistCm, heightCm)
        return resultFor(waist = waistCm, height = heightCm)
    }

    fun calculateImperial(waistInches: Double, heightInches: Double): WaistToHeightResult {
        InputValidator.requireImperialWaistAndHeight(waistInches, heightInches)
        return resultFor(waist = waistInches, height = heightInches)
    }

    private fun resultFor(waist: Double, height: Double): WaistToHeightResult {
        val ratio = waist / height
        return WaistToHeightResult(
            ratio = ratio,
            displayRatio = round(ratio * 100.0) / 100.0,
        )
    }
}
