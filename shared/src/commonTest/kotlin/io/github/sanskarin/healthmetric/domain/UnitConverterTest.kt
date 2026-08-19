package io.github.sanskarin.healthmetric.domain

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class UnitConverterTest {
    @Test
    fun poundsRoundTripIsStable() {
        val original = 180.0
        val roundTrip = UnitConverter.kilogramsToPounds(UnitConverter.poundsToKilograms(original))
        assertTrue(abs(original - roundTrip) < 0.000001)
    }

    @Test
    fun sixFeetConvertsToExpectedCentimeters() {
        val centimeters = UnitConverter.imperialHeightToCentimeters(6, 0.0)
        assertTrue(abs(182.88 - centimeters) < 0.000001)
    }
}
