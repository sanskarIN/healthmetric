package io.github.sanskarin.healthmetric.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Test

class AppPreferencesTest {
    @Test
    fun freshPreferencesKeepHistoryDisabled() {
        val preferences = AppPreferences()

        assertFalse(preferences.historyEnabled)
        assertEquals(AppThemeMode.SYSTEM, preferences.themeMode)
        assertFalse(preferences.adultUseConfirmed)
        assertFalse(preferences.onboardingComplete)
    }
}
