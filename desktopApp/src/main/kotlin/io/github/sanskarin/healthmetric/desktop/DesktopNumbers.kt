package io.github.sanskarin.healthmetric.desktop

internal object DesktopNumbers {
    fun parseDecimal(raw: String): Double? {
        val normalized = raw.trim().replace(',', '.')
        if (normalized.isEmpty() || normalized.count { it == '.' } > 1) return null
        return normalized.toDoubleOrNull()?.takeIf(Double::isFinite)
    }

    fun parseWholeNumber(raw: String): Int? = raw.trim().toIntOrNull()
}
