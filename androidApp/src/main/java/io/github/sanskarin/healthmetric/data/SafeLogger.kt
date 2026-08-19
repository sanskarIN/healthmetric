package io.github.sanskarin.healthmetric.data

import android.util.Log

/**
 * Minimal structured logger that intentionally never accepts measurement values,
 * backup contents, email addresses, tokens, or other arbitrary user-controlled text.
 */
object SafeLogger {
    enum class Event {
        HISTORY_CLEARED,
        ALL_LOCAL_DATA_CLEARED,
        EXPORT_FAILED,
        RESTORE_FAILED,
        LINK_OPEN_FAILED,
    }

    fun info(event: Event) {
        Log.i(TAG, "event=${event.name.lowercase()}")
    }

    fun warn(event: Event, throwable: Throwable? = null) {
        val errorType = throwable?.javaClass?.simpleName
            ?.filter { it.isLetterOrDigit() || it == '_' }
            ?.take(64)
            ?: "unknown"
        Log.w(TAG, "event=${event.name.lowercase()} error_type=$errorType")
    }

    private const val TAG = "HealthMetric"
}
