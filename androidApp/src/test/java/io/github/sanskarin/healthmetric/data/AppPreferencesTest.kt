package io.github.sanskarin.healthmetric.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppPreferencesTest {
    @Test
    fun freshPreferencesKeepHistoryDisabled() {
        val preferences = AppPreferences()

        assertFalse(preferences.historyEnabled)
        assertEquals(HistoryRetentionPolicy.DEFAULT_LIMIT, preferences.historyRetentionLimit)
        assertEquals(AppThemeMode.SYSTEM, preferences.themeMode)
        assertFalse(preferences.adultUseConfirmed)
        assertFalse(preferences.onboardingComplete)
    }

    @Test
    fun retentionPolicyAcceptsOnlySupportedLimits() {
        HistoryRetentionPolicy.allowedLimits.forEach { limit ->
            assertEquals(limit, HistoryRetentionPolicy.normalize(limit))
        }

        assertEquals(HistoryRetentionPolicy.DEFAULT_LIMIT, HistoryRetentionPolicy.normalize(0))
        assertEquals(HistoryRetentionPolicy.DEFAULT_LIMIT, HistoryRetentionPolicy.normalize(999))
    }
}
