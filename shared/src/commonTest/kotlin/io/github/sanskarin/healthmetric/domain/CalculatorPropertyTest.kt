package io.github.sanskarin.healthmetric.domain

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class CalculatorPropertyTest {
    @Test
    fun validAdultMetricInputsAlwaysProduceFinitePositiveBmi() {
        val random = Random(20260819)

        repeat(1_000) {
            val weight = random.nextDouble(20.0, 500.0)
            val height = random.nextDouble(100.0, 250.0)
            val result = BmiCalculator.calculateMetric(MetricBodyInput(weight, height))

            assertTrue(result.bmi.isFinite())
            assertTrue(result.bmi > 0.0)
            assertTrue(result.reference.adultOnly)
        }
    }

    @Test
    fun validWaistAndHeightInputsAlwaysProduceFinitePositiveRatio() {
        val random = Random(20260819)

        repeat(1_000) {
            val waist = random.nextDouble(30.0, 250.0)
            val height = random.nextDouble(100.0, 250.0)
            val result = WaistToHeightCalculator.calculateMetric(waist, height)

            assertTrue(result.ratio.isFinite())
            assertTrue(result.ratio > 0.0)
        }
    }
}
