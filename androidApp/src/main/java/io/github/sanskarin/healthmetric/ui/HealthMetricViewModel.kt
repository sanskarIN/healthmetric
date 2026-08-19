package io.github.sanskarin.healthmetric.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.sanskarin.healthmetric.data.AppPreferences
import io.github.sanskarin.healthmetric.data.AppThemeMode
import io.github.sanskarin.healthmetric.data.CalculatorKind
import io.github.sanskarin.healthmetric.data.HealthMetricDataStore
import io.github.sanskarin.healthmetric.data.HistoryEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class HealthMetricUiState(
    val preferences: AppPreferences = AppPreferences(),
    val history: List<HistoryEntry> = emptyList(),
    val isReady: Boolean = false,
)

class HealthMetricViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = HealthMetricDataStore(application.applicationContext)

    val uiState = combine(dataStore.preferences, dataStore.history) { preferences, history ->
        HealthMetricUiState(
            preferences = preferences,
            history = history,
            isReady = true,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HealthMetricUiState(),
    )

    fun completeOnboarding(adultUseConfirmed: Boolean) {
        viewModelScope.launch { dataStore.completeOnboarding(adultUseConfirmed) }
    }

    fun setHistoryEnabled(enabled: Boolean) {
        viewModelScope.launch { dataStore.setHistoryEnabled(enabled) }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch { dataStore.setThemeMode(mode) }
    }

    fun recordCalculation(kind: CalculatorKind, value: Double, summary: String) {
        if (!value.isFinite()) return
        viewModelScope.launch {
            val timestamp = System.currentTimeMillis()
            dataStore.addHistory(
                HistoryEntry(
                    id = "$timestamp-${Random.nextInt(100_000, 999_999)}",
                    timestampEpochMillis = timestamp,
                    calculator = kind,
                    value = value,
                    summary = summary.take(240),
                ),
            )
        }
    }

    fun deleteHistory() {
        viewModelScope.launch { dataStore.deleteHistory() }
    }

    fun deleteAllLocalData() {
        viewModelScope.launch { dataStore.deleteAllLocalData() }
    }

    fun exportData(onReady: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            runCatching { dataStore.exportJson() }
                .onSuccess(onReady)
                .onFailure { onError("Could not export local data.") }
        }
    }

    fun restoreData(rawJson: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            runCatching { dataStore.restoreFromJson(rawJson) }
                .onSuccess { onComplete(true, "Local backup restored.") }
                .onFailure { onComplete(false, "The selected backup is invalid or unsupported.") }
        }
    }
}
