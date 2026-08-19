package io.github.sanskarin.healthmetric.ui.format

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalizedNumbersTest {
    @Test
    fun acceptsDotAndCommaAsSingleDecimalSeparators() {
        assertTrue(LocalizedNumbers.isValidInput("72.5", wholeNumbersOnly = false, maxLength = 12))
        assertTrue(LocalizedNumbers.isValidInput("72,5", wholeNumbersOnly = false, maxLength = 12))
        assertFalse(LocalizedNumbers.isValidInput("72.5,1", wholeNumbersOnly = false, maxLength = 12))
        assertFalse(LocalizedNumbers.isValidInput("72..5", wholeNumbersOnly = false, maxLength = 12))
        assertFalse(LocalizedNumbers.isValidInput("72kg", wholeNumbersOnly = false, maxLength = 12))
    }

    @Test
    fun parsesLocaleDecimalSeparatorAndFallbackSeparator() {
        assertEquals(72.5, LocalizedNumbers.parseDecimal("72.5", Locale.US)!!, 0.0001)
        assertEquals(72.5, LocalizedNumbers.parseDecimal("72,5", Locale.GERMANY)!!, 0.0001)
        assertEquals(72.5, LocalizedNumbers.parseDecimal("72.5", Locale.GERMANY)!!, 0.0001)
        assertEquals(72.5, LocalizedNumbers.parseDecimal("72,5", Locale.US)!!, 0.0001)
    }

    @Test
    fun rejectsInvalidAndNonFiniteInput() {
        assertNull(LocalizedNumbers.parseDecimal("", Locale.US))
        assertNull(LocalizedNumbers.parseDecimal("7,2.5", Locale.US))
        assertNull(LocalizedNumbers.parseDecimal("NaN", Locale.US))
        assertNull(LocalizedNumbers.parseDecimal("Infinity", Locale.US))
    }

    @Test
    fun formatsWithoutGroupingUsingRequestedLocale() {
        assertEquals("22.9", LocalizedNumbers.format(22.9, maximumFractionDigits = 1, locale = Locale.US))
        assertEquals("22,9", LocalizedNumbers.format(22.9, maximumFractionDigits = 1, locale = Locale.GERMANY))
        assertEquals("0,47", LocalizedNumbers.format(0.47, maximumFractionDigits = 2, locale = Locale.GERMANY))
    }
}
