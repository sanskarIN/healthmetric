package io.github.sanskarin.healthmetric

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HealthMetric",
        state = WindowState(size = DpSize(980.dp, 760.dp)),
    ) {
        App()
    }
}
