package io.github.sanskarin.healthmetric.ui.format

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

object LocalizedNumbers {
    fun isValidInput(
        candidate: String,
        wholeNumbersOnly: Boolean,
        maxLength: Int,
    ): Boolean {
        if (candidate.length > maxLength) return false
        if (candidate.isEmpty()) return true
        if (wholeNumbersOnly) return candidate.all(Char::isDigit)

        var separatorCount = 0
        return candidate.all { character ->
            when {
                character.isDigit() -> true
                character == '.' || character == ',' -> {
                    separatorCount += 1
                    separatorCount <= 1
                }
                else -> false
            }
        }
    }

    fun parseDecimal(text: String, locale: Locale = Locale.getDefault()): Double? {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return null
        if (!isValidInput(trimmed, wholeNumbersOnly = false, maxLength = Int.MAX_VALUE)) return null

        val localeSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
        val inputSeparator = when {
            trimmed.contains(localeSeparator) -> localeSeparator
            localeSeparator != '.' && trimmed.contains('.') -> '.'
            localeSeparator != ',' && trimmed.contains(',') -> ','
            else -> null
        }
        val normalized = if (inputSeparator == null || inputSeparator == '.') {
            trimmed
        } else {
            trimmed.replace(inputSeparator, '.')
        }

        return normalized.toDoubleOrNull()?.takeIf(Double::isFinite)
    }

    fun format(
        value: Double,
        maximumFractionDigits: Int,
        locale: Locale = Locale.getDefault(),
    ): String {
        require(value.isFinite()) { "Only finite numbers can be formatted." }
        require(maximumFractionDigits >= 0) { "Fraction digits cannot be negative." }

        val formatter = NumberFormat.getNumberInstance(locale).apply {
            isGroupingUsed = false
            minimumFractionDigits = 0
            this.maximumFractionDigits = maximumFractionDigits
            if (this is DecimalFormat) {
                isParseBigDecimal = false
            }
        }
        return formatter.format(value)
    }
}
