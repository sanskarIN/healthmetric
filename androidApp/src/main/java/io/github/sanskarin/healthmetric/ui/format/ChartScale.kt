package io.github.sanskarin.healthmetric.ui.format

import kotlin.math.abs

internal object ChartScale {
    fun normalize(values: List<Double>): List<Float> {
        if (values.isEmpty()) return emptyList()
        require(values.all(Double::isFinite)) { "Chart values must be finite." }

        val minValue = values.minOrNull() ?: return emptyList()
        val maxValue = values.maxOrNull() ?: return emptyList()
        val scale = maxOf(abs(minValue), abs(maxValue), 1.0)
        val scaledMin = minValue / scale
        val scaledMax = maxValue / scale
        val scaledRange = scaledMax - scaledMin

        if (!scaledRange.isFinite() || scaledRange <= MIN_RANGE) {
            return List(values.size) { CENTER }
        }

        return values.map { value ->
            (((value / scale) - scaledMin) / scaledRange)
                .coerceIn(0.0, 1.0)
                .toFloat()
        }
    }

    private const val MIN_RANGE = 0.000000000001
    private const val CENTER = 0.5f
}
