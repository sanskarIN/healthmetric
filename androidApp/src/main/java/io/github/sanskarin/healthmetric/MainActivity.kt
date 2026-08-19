package io.github.sanskarin.healthmetric

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.sanskarin.healthmetric.ui.HealthMetricApp
import io.github.sanskarin.healthmetric.ui.HealthMetricViewModel
import io.github.sanskarin.healthmetric.ui.theme.HealthMetricTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: HealthMetricViewModel = viewModel()
            val state by viewModel.uiState.collectAsState()

            HealthMetricTheme(themeMode = state.preferences.themeMode) {
                HealthMetricApp(
                    state = state,
                    viewModel = viewModel,
                )
            }
        }
    }
}
