package io.github.sanskarin.healthmetric.domain

enum class UnitSystem {
    METRIC,
    IMPERIAL,
}

data class MetricBodyInput(
    val weightKg: Double,
    val heightCm: Double,
)

data class ImperialBodyInput(
    val weightLb: Double,
    val heightFeet: Int,
    val heightInches: Double,
)

object UnitConverter {
    const val KG_PER_POUND: Double = 0.45359237
    const val CM_PER_INCH: Double = 2.54

    fun poundsToKilograms(pounds: Double): Double = pounds * KG_PER_POUND

    fun kilogramsToPounds(kilograms: Double): Double = kilograms / KG_PER_POUND

    fun inchesToCentimeters(inches: Double): Double = inches * CM_PER_INCH

    fun centimetersToInches(centimeters: Double): Double = centimeters / CM_PER_INCH

    fun imperialHeightToCentimeters(feet: Int, inches: Double): Double =
        inchesToCentimeters((feet * 12.0) + inches)
}
