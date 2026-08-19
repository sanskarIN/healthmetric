package io.github.sanskarin.healthmetric.domain

sealed class ValidationError(message: String) : IllegalArgumentException(message) {
    data object WeightOutOfRange : ValidationError("Weight must be between 20 kg and 500 kg for this adult educational calculator.")
    data object ImperialWeightOutOfRange : ValidationError("Weight must be between 44 lb and 1102.5 lb for this adult educational calculator.")
    data object HeightOutOfRange : ValidationError("Height must be between 100 cm and 250 cm for this adult educational calculator.")
    data object ImperialHeightOutOfRange : ValidationError("Imperial height must resolve to between 100 cm and 250 cm.")
    data object WaistOutOfRange : ValidationError("Waist measurement must be between 30 cm and 250 cm.")
    data object NonFiniteNumber : ValidationError("Measurements must be finite numbers.")
}

object InputValidator {
    fun requireMetricBodyInput(input: MetricBodyInput) {
        requireFinite(input.weightKg, input.heightCm)
        if (input.weightKg !in 20.0..500.0) throw ValidationError.WeightOutOfRange
        if (input.heightCm !in 100.0..250.0) throw ValidationError.HeightOutOfRange
    }

    fun requireImperialBodyInput(input: ImperialBodyInput) {
        requireFinite(input.weightLb, input.heightInches)
        if (input.weightLb !in 44.0..1102.5) throw ValidationError.ImperialWeightOutOfRange
        if (input.heightFeet !in 0..8 || input.heightInches !in 0.0..<12.0) {
            throw ValidationError.ImperialHeightOutOfRange
        }
        val heightCm = UnitConverter.imperialHeightToCentimeters(input.heightFeet, input.heightInches)
        if (heightCm !in 100.0..250.0) throw ValidationError.ImperialHeightOutOfRange
    }

    fun requireWaistAndHeight(waistCm: Double, heightCm: Double) {
        requireFinite(waistCm, heightCm)
        if (waistCm !in 30.0..250.0) throw ValidationError.WaistOutOfRange
        if (heightCm !in 100.0..250.0) throw ValidationError.HeightOutOfRange
    }

    private fun requireFinite(vararg values: Double) {
        if (values.any { !it.isFinite() }) throw ValidationError.NonFiniteNumber
    }
}
