package io.github.sanskarin.healthmetric.desktop

internal object DesktopNumbers {
    private val decimalPattern = Regex("(?:[0-9]+(?:[.,][0-9]*)?|[.,][0-9]+)")
    private val wholeNumberPattern = Regex("[0-9]+")

    fun parseDecimal(raw: String): Double? {
        val trimmed = raw.trim()
        if (!decimalPattern.matches(trimmed)) return null

        return trimmed
            .replace(',', '.')
            .toDoubleOrNull()
            ?.takeIf(Double::isFinite)
    }

    fun parseWholeNumber(raw: String): Int? {
        val trimmed = raw.trim()
        if (!wholeNumberPattern.matches(trimmed)) return null
        return trimmed.toIntOrNull()
    }
}
