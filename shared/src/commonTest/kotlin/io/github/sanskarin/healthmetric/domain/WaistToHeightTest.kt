package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WaistToHeightTest {
    @Test
    fun metricRatioIsCalculatedAndRounded() {
        val result = WaistToHeightCalculator.calculateMetric(
            waistCm = 80.0,
            heightCm = 180.0,
        )

        assertEquals(0.44, result.displayRatio)
    }

    @Test
    fun imperialAndMetricInputsProduceSameRoundedRatio() {
        val metric = WaistToHeightCalculator.calculateMetric(76.2, 177.8)
        val imperial = WaistToHeightCalculator.calculateImperial(30.0, 70.0)

        assertEquals(metric.displayRatio, imperial.displayRatio)
    }

    @Test
    fun imperialDocumentedBoundariesRemainAccepted() {
        val minimumWaist = UnitConverter.centimetersToInches(30.0)
        val maximumWaist = UnitConverter.centimetersToInches(250.0)
        val minimumHeight = UnitConverter.centimetersToInches(100.0)
        val maximumHeight = UnitConverter.centimetersToInches(250.0)

        val lowerBoundary = WaistToHeightCalculator.calculateImperial(
            waistInches = minimumWaist,
            heightInches = minimumHeight,
        )
        val upperBoundary = WaistToHeightCalculator.calculateImperial(
            waistInches = maximumWaist,
            heightInches = maximumHeight,
        )

        assertTrue(lowerBoundary.ratio.isFinite())
        assertTrue(upperBoundary.ratio.isFinite())
        assertEquals(0.30, lowerBoundary.displayRatio)
        assertEquals(1.00, upperBoundary.displayRatio)
    }
}
