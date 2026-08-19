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
        val ratio = waistCm / heightCm
        return WaistToHeightResult(
            ratio = ratio,
            displayRatio = round(ratio * 100.0) / 100.0,
        )
    }

    fun calculateImperial(waistInches: Double, heightInches: Double): WaistToHeightResult {
        if (!waistInches.isFinite() || !heightInches.isFinite()) throw ValidationError.NonFiniteNumber
        val waistCm = UnitConverter.inchesToCentimeters(waistInches)
        val heightCm = UnitConverter.inchesToCentimeters(heightInches)
        return calculateMetric(waistCm, heightCm)
    }
}
