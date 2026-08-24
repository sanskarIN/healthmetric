package io.github.sanskarin.healthmetric.ui

import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementInputTest {
    @Test
    fun `decimal sanitizer accepts dot and comma separators`() {
        assertEquals("72.5", MeasurementInput.sanitizeDecimal("72.5"))
        assertEquals("72.5", MeasurementInput.sanitizeDecimal("72,5"))
    }

    @Test
    fun `decimal sanitizer removes unrelated characters and extra separators`() {
        assertEquals("72.50", MeasurementInput.sanitizeDecimal("72kg,5.0"))
    }

    @Test
    fun `whole number sanitizer enforces the requested length`() {
        assertEquals("123", MeasurementInput.sanitizeWholeNumber("1a2b34", maxLength = 3))
    }

    @Test
    fun `imperial height composition converts feet plus inches to total inches`() {
        assertEquals(69.0, MeasurementInput.imperialHeightInches(feet = 5, additionalInches = 9.0))
    }
}
