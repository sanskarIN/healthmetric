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
        val safeEntry = sanitizeEntry(entry)
        context.healthMetricDataStore.edit { preferences ->
            if (preferences[Keys.historyEnabled] != true) return@edit
            val retentionLimit = HistoryRetentionPolicy.normalize(
                preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
            )
            val current = decodeHistory(preferences[Keys.historyJson]).toMutableList()
            current.removeAll { it.id == safeEntry.id }
            current.add(0, safeEntry)
            preferences[Keys.historyJson] = encodeHistory(current.take(retentionLimit))
        }
    }

    suspend fun restoreHistoryEntry(entry: HistoryEntry) {
        val safeEntry = sanitizeEntry(entry)
        context.healthMetricDataStore.edit { preferences ->
            val retentionLimit = HistoryRetentionPolicy.normalize(
                preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
            )
            val current = decodeHistory(preferences[Keys.historyJson]).toMutableList()
            current.removeAll { it.id == safeEntry.id }
            current.add(0, safeEntry)
            preferences[Keys.historyJson] = encodeHistory(current.take(retentionLimit))
        }
    }

    suspend fun deleteHistoryEntry(id: String) {
        if (id.isBlank()) return
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
            put(
                "historyRetentionLimit",
                HistoryRetentionPolicy.normalize(
                    preferences[Keys.historyRetentionLimit] ?: HistoryRetentionPolicy.DEFAULT_LIMIT,
                ),
            )
            put("themeMode", preferences[Keys.themeMode] ?: AppThemeMode.SYSTEM.name)
            put("history", JSONArray(encodeHistory(decodeHistory(preferences[Keys.historyJson]))))
        }.toString(2)
    }

    suspend fun restoreFromJson(rawJson: String) {
        require(rawJson.toByteArray(Charsets.UTF_8).size <= BackupIo.MAX_BACKUP_BYTES) {
            "Backup data is too large."
        }

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
            preferences[Keys.historyRetentionLimit] = restoredRetentionLimit
            preferences[Keys.themeMode] = restoredTheme.name
            preferences[Keys.historyJson] = encodeHistory(restoredHistory)
            // History opt-in, adult-use confirmation, and onboarding state are intentionally
            // device-local consent/safety state. Portable backups must never change them.
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
        val array = runCatching { JSONArray(rawJson) }.getOrElse { return emptyList() }
        val seenIds = mutableSetOf<String>()

        return buildList {
            for (index in 0 until array.length()) {
                val entry = runCatching {
                    val item = array.getJSONObject(index)
                    sanitizeEntry(
                        HistoryEntry(
                            id = item.getString("id"),
                            timestampEpochMillis = item.getLong("timestampEpochMillis"),
                            calculator = CalculatorKind.valueOf(item.getString("calculator")),
                            value = item.getDouble("value"),
                            summary = item.optString("summary", ""),
                        ),
                    )
                }.getOrNull() ?: continue

                if (seenIds.add(entry.id)) add(entry)
                if (size >= HistoryRetentionPolicy.MAX_LIMIT) break
            }
        }
    }

    private fun sanitizeEntry(entry: HistoryEntry): HistoryEntry {
        val id = entry.id.trim().take(MAX_ID_LENGTH)
        require(id.isNotBlank()) { "History entry ID cannot be blank." }
        require(entry.timestampEpochMillis >= 0L) { "History timestamp cannot be negative." }
        require(entry.value.isFinite()) { "History value must be finite." }

        return entry.copy(
            id = id,
            summary = entry.summary.take(MAX_SUMMARY_LENGTH),
        )
    }

    companion object {
        private const val BACKUP_SCHEMA_VERSION = 1
        private const val MAX_SUMMARY_LENGTH = 240
        private const val MAX_ID_LENGTH = 96
    }
}
