package io.github.sanskarin.healthmetric

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

class HealthMetricViewControllerFactory {
    fun makeViewController(): UIViewController = ComposeUIViewController {
        App()
    }
}
