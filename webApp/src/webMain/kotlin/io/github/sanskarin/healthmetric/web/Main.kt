package io.github.sanskarin.healthmetric.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.sanskarin.healthmetric.ui.HealthMetricCrossPlatformApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(viewportContainerId = "webApp") {
        HealthMetricCrossPlatformApp()
    }
}
