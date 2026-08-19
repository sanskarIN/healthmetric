package io.github.sanskarin.healthmetric.ui.format

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ChartScaleTest {
    @Test
    fun normalizesOrderedValuesAcrossUnitRange() {
        assertEquals(
            listOf(0.0f, 0.5f, 1.0f),
            ChartScale.normalize(listOf(10.0, 20.0, 30.0)),
        )
    }

    @Test
    fun centersFlatSeries() {
        assertEquals(
            listOf(0.5f, 0.5f, 0.5f),
            ChartScale.normalize(listOf(22.9, 22.9, 22.9)),
        )
    }

    @Test
    fun extremeFiniteValuesDoNotOverflowNormalization() {
        assertEquals(
            listOf(0.0f, 0.5f, 1.0f),
            ChartScale.normalize(listOf(-Double.MAX_VALUE, 0.0, Double.MAX_VALUE)),
        )
    }

    @Test
    fun emptySeriesStaysEmpty() {
        assertEquals(emptyList<Float>(), ChartScale.normalize(emptyList()))
    }

    @Test
    fun rejectsNonFiniteValues() {
        assertThrows(IllegalArgumentException::class.java) {
            ChartScale.normalize(listOf(1.0, Double.POSITIVE_INFINITY))
        }
    }
}
