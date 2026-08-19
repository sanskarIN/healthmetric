package io.github.sanskarin.healthmetric.desktop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DesktopNumbersTest {
    @Test
    fun parsesDotAndCommaDecimals() {
        assertEquals(72.5, DesktopNumbers.parseDecimal("72.5"))
        assertEquals(72.5, DesktopNumbers.parseDecimal("72,5"))
    }

    @Test
    fun trimsWhitespace() {
        assertEquals(181.25, DesktopNumbers.parseDecimal(" 181.25 "))
        assertEquals(6, DesktopNumbers.parseWholeNumber(" 6 "))
    }

    @Test
    fun rejectsMalformedAndNonFiniteDecimals() {
        assertNull(DesktopNumbers.parseDecimal(""))
        assertNull(DesktopNumbers.parseDecimal("72..5"))
        assertNull(DesktopNumbers.parseDecimal("not-a-number"))
        assertNull(DesktopNumbers.parseDecimal("NaN"))
        assertNull(DesktopNumbers.parseDecimal("Infinity"))
    }

    @Test
    fun rejectsNonWholeFeetInput() {
        assertNull(DesktopNumbers.parseWholeNumber("5.5"))
        assertNull(DesktopNumbers.parseWholeNumber("five"))
    }
}
