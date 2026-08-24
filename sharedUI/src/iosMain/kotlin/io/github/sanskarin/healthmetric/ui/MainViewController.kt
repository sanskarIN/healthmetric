package io.github.sanskarin.healthmetric.ui

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController =
    ComposeUIViewController {
        HealthMetricCrossPlatformApp()
    }
