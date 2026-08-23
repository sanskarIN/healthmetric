package io.github.sanskarin.healthmetric.ui

internal object MeasurementInput {
    fun sanitizeWholeNumber(value: String, maxLength: Int): String =
        value.filter(Char::isDigit).take(maxLength)

    fun sanitizeDecimal(value: String): String {
        var separatorSeen = false
        return buildString {
            value.forEach { character ->
                when {
                    character.isDigit() -> append(character)
                    (character == '.' || character == ',') && !separatorSeen -> {
                        append('.')
                        separatorSeen = true
                    }
                }
            }
        }
    }

    fun imperialHeightInches(feet: Int, additionalInches: Double): Double =
        (feet * 12.0) + additionalInches
}
