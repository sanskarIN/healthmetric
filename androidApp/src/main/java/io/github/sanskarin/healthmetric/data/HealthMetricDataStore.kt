package io.github.sanskarin.healthmetric.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val historyRetentionLimit = intPreferencesKey("history_retention_limit")
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

    suspend fun setHistoryRetentionLimit(limit: Int) {
        val normalizedLimit = HistoryRetentionPolicy.normalize(limit)
        context.healthMetricDataStore.edit { preferences ->
            preferences[Keys.historyRetentionLimit] = normalizedLimit
            val current = decodeHistory(preferences[Keys.historyJson])
            preferences[Keys.historyJson] = encodeHistory(current.take(normalizedLimit))
        }
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
            if (preferences[Keys.historyEnabled] != true) return@edit
            val retentionLimit = HistoryRetentionPolicy.normalize(
                preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
            )
            val current = decodeHistory(preferences[Keys.historyJson]).toMutableList()
            current.removeAll { it.id == entry.id }
            current.add(0, sanitizeEntry(entry))
            preferences[Keys.historyJson] = encodeHistory(current.take(retentionLimit))
        }
    }

    suspend fun restoreHistoryEntry(entry: HistoryEntry) {
        context.healthMetricDataStore.edit { preferences ->
            val retentionLimit = HistoryRetentionPolicy.normalize(
                preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
            )
            val current = decodeHistory(preferences[Keys.historyJson]).toMutableList()
            current.removeAll { it.id == entry.id }
            current.add(0, sanitizeEntry(entry))
            preferences[Keys.historyJson] = encodeHistory(current.take(retentionLimit))
        }
    }

    suspend fun deleteHistoryEntry(id: String) {
        context.healthMetricDataStore.edit { preferences ->
            val current = decodeHistory(preferences[Keys.historyJson])
            preferences[Keys.historyJson] = encodeHistory(current.filterNot { it.id == id })
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
            put("schemaVersion", BACKUP_SCHEMA_VERSION)
            put("historyEnabled", preferences[Keys.historyEnabled] ?: false)
            put(
                "historyRetentionLimit",
                HistoryRetentionPolicy.normalize(
                    preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
                ),
            )
            put("themeMode", preferences[Keys.themeMode] ?: AppThemeMode.SYSTEM.name)
            put("adultUseConfirmed", preferences[Keys.adultUseConfirmed] ?: false)
            put("onboardingComplete", preferences[Keys.onboardingComplete] ?: false)
            put("history", JSONArray(encodeHistory(decodeHistory(preferences[Keys.historyJson]))))
        }.toString(2)
    }

    suspend fun restoreFromJson(rawJson: String) {
        val root = JSONObject(rawJson)
        require(root.optInt("schemaVersion", -1) == BACKUP_SCHEMA_VERSION) {
            "Unsupported backup schema."
        }

        val restoredTheme = runCatching {
            AppThemeMode.valueOf(root.optString("themeMode", AppThemeMode.SYSTEM.name))
        }.getOrDefault(AppThemeMode.SYSTEM)
        val restoredRetentionLimit = HistoryRetentionPolicy.normalize(
            root.optInt("historyRetentionLimit", HistoryRetentionPolicy.DEFAULT_LIMIT),
        )
        val restoredHistory = decodeHistory(root.optJSONArray("history")?.toString())
            .take(restoredRetentionLimit)

        context.healthMetricDataStore.edit { preferences ->
            preferences[Keys.historyEnabled] = root.optBoolean("historyEnabled", false)
            preferences[Keys.historyRetentionLimit] = restoredRetentionLimit
            preferences[Keys.themeMode] = restoredTheme.name
            preferences[Keys.adultUseConfirmed] = root.optBoolean("adultUseConfirmed", false)
            preferences[Keys.onboardingComplete] = root.optBoolean("onboardingComplete", false)
            preferences[Keys.historyJson] = encodeHistory(restoredHistory)
        }
    }

    private fun decodePreferences(preferences: Preferences): AppPreferences = AppPreferences(
        historyEnabled = preferences[Keys.historyEnabled] ?: false,
        historyRetentionLimit = HistoryRetentionPolicy.normalize(
            preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
        ),
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
                        sanitizeEntry(
                            HistoryEntry(
                                id = item.getString("id"),
                                timestampEpochMillis = item.getLong("timestampEpochMillis"),
                                calculator = calculator,
                                value = value,
                                summary = item.getString("summary"),
                            ),
                        ),
                    )
                }
            }.take(HistoryRetentionPolicy.MAX_LIMIT)
        }.getOrElse { emptyList() }
    }

    private fun sanitizeEntry(entry: HistoryEntry): HistoryEntry = entry.copy(
        id = entry.id.take(MAX_ID_LENGTH),
        summary = entry.summary.take(MAX_SUMMARY_LENGTH),
    )

    companion object {
        private const val BACKUP_SCHEMA_VERSION = 1
        private const val MAX_SUMMARY_LENGTH = 240
        private const val MAX_ID_LENGTH = 96
    }
}
