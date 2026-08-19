package io.github.sanskarin.healthmetric.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.healthMetricDataStore by preferencesDataStore(name = "healthmetric")

class HealthMetricDataStore(private val context: Context) {
    private object Keys {
        val historyEnabled = booleanPreferencesKey("history_enabled")
        val themeMode = stringPreferencesKey("theme_mode")
        val adultUseConfirmed = booleanPreferencesKey("adult_use_confirmed")
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
        val historyJson = stringPreferencesKey("history_json")
    }

    val preferences: Flow<AppPreferences> = context.healthMetricDataStore.data.map(::decodePreferences)

    val history: Flow<List<HistoryEntry>> = context.healthMetricDataStore.data.map { preferences ->
        decodeHistory(preferences[Keys.historyJson])
    }

    suspend fun setHistoryEnabled(enabled: Boolean) {
        context.healthMetricDataStore.edit { it[Keys.historyEnabled] = enabled }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.healthMetricDataStore.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun completeOnboarding(adultUseConfirmed: Boolean) {
        context.healthMetricDataStore.edit {
            it[Keys.adultUseConfirmed] = adultUseConfirmed
            it[Keys.onboardingComplete] = true
        }
    }

    suspend fun addHistory(entry: HistoryEntry) {
        context.healthMetricDataStore.edit { preferences ->
            if (preferences[Keys.historyEnabled] == false) return@edit
            val current = decodeHistory(preferences[Keys.historyJson]).toMutableList()
            current.add(0, entry)
            preferences[Keys.historyJson] = encodeHistory(current.take(MAX_HISTORY_ITEMS))
        }
    }

    suspend fun deleteHistory() {
        context.healthMetricDataStore.edit { it.remove(Keys.historyJson) }
    }

    suspend fun deleteAllLocalData() {
        context.healthMetricDataStore.edit { it.clear() }
    }

    suspend fun exportJson(): String {
        val preferences = context.healthMetricDataStore.data.first()
        return JSONObject().apply {
            put("schemaVersion", 1)
            put("historyEnabled", preferences[Keys.historyEnabled] ?: true)
            put("themeMode", preferences[Keys.themeMode] ?: AppThemeMode.SYSTEM.name)
            put("adultUseConfirmed", preferences[Keys.adultUseConfirmed] ?: false)
            put("onboardingComplete", preferences[Keys.onboardingComplete] ?: false)
            put("history", JSONArray(encodeHistory(decodeHistory(preferences[Keys.historyJson]))))
        }.toString(2)
    }

    suspend fun restoreFromJson(rawJson: String) {
        val root = JSONObject(rawJson)
        require(root.optInt("schemaVersion", -1) == 1) { "Unsupported backup schema." }

        val restoredHistory = decodeHistory(root.optJSONArray("history")?.toString())
        val restoredTheme = runCatching {
            AppThemeMode.valueOf(root.optString("themeMode", AppThemeMode.SYSTEM.name))
        }.getOrDefault(AppThemeMode.SYSTEM)

        context.healthMetricDataStore.edit { preferences ->
            preferences[Keys.historyEnabled] = root.optBoolean("historyEnabled", true)
            preferences[Keys.themeMode] = restoredTheme.name
            preferences[Keys.adultUseConfirmed] = root.optBoolean("adultUseConfirmed", false)
            preferences[Keys.onboardingComplete] = root.optBoolean("onboardingComplete", false)
            preferences[Keys.historyJson] = encodeHistory(restoredHistory.take(MAX_HISTORY_ITEMS))
        }
    }

    private fun decodePreferences(preferences: Preferences): AppPreferences = AppPreferences(
        historyEnabled = preferences[Keys.historyEnabled] ?: true,
        themeMode = runCatching {
            AppThemeMode.valueOf(preferences[Keys.themeMode] ?: AppThemeMode.SYSTEM.name)
        }.getOrDefault(AppThemeMode.SYSTEM),
        adultUseConfirmed = preferences[Keys.adultUseConfirmed] ?: false,
        onboardingComplete = preferences[Keys.onboardingComplete] ?: false,
    )

    private fun encodeHistory(entries: List<HistoryEntry>): String = JSONArray().apply {
        entries.forEach { entry ->
            put(JSONObject().apply {
                put("id", entry.id)
                put("timestampEpochMillis", entry.timestampEpochMillis)
                put("calculator", entry.calculator.name)
                put("value", entry.value)
                put("summary", entry.summary)
            })
        }
    }.toString()

    private fun decodeHistory(rawJson: String?): List<HistoryEntry> {
        if (rawJson.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(rawJson)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    val calculator = CalculatorKind.valueOf(item.getString("calculator"))
                    val value = item.getDouble("value")
                    if (!value.isFinite()) continue
                    add(
                        HistoryEntry(
                            id = item.getString("id").take(MAX_ID_LENGTH),
                            timestampEpochMillis = item.getLong("timestampEpochMillis"),
                            calculator = calculator,
                            value = value,
                            summary = item.getString("summary").take(MAX_SUMMARY_LENGTH),
                        ),
                    )
                }
            }
        }.getOrElse { emptyList() }
    }

    companion object {
        private const val MAX_HISTORY_ITEMS = 500
        private const val MAX_SUMMARY_LENGTH = 240
        private const val MAX_ID_LENGTH = 96
    }
}
