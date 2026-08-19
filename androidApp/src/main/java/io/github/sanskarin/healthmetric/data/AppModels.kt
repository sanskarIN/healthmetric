package io.github.sanskarin.healthmetric.data

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

enum class CalculatorKind {
    BMI,
    WAIST_TO_HEIGHT,
}

data class HistoryEntry(
    val id: String,
    val timestampEpochMillis: Long,
    val calculator: CalculatorKind,
    val value: Double,
    val summary: String,
)

data class AppPreferences(
    val historyEnabled: Boolean = false,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val adultUseConfirmed: Boolean = false,
    val onboardingComplete: Boolean = false,
)
