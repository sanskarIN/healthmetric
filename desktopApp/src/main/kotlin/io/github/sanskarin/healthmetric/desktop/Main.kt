package io.github.sanskarin.healthmetric.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.sanskarin.healthmetric.ui.HealthMetricCrossPlatformApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HealthMetric",
    ) {
        HealthMetricCrossPlatformApp()
    }
}
