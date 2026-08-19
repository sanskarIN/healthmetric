package io.github.sanskarin.healthmetric.domain

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
