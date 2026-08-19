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

object HistoryRetentionPolicy {
    const val DEFAULT_LIMIT: Int = 100
    const val MAX_LIMIT: Int = 500

    val allowedLimits: List<Int> = listOf(50, 100, 250, MAX_LIMIT)

    fun normalize(limit: Int): Int = if (limit in allowedLimits) limit else DEFAULT_LIMIT
}

data class AppPreferences(
    val historyEnabled: Boolean = false,
    val historyRetentionLimit: Int = HistoryRetentionPolicy.DEFAULT_LIMIT,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val adultUseConfirmed: Boolean = false,
    val onboardingComplete: Boolean = false,
)
