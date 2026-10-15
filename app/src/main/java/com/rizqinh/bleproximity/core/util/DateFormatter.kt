package com.rizqinh.bleproximity.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for formatting timestamps into human-readable date/time strings.
 */
object DateFormatter {

    /**
     * Formats timestamp (ms) to HH:mm:ss format (e.g. "14:25:08").
     */
    fun formatTimeString(timestampMs: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestampMs))
    }

    /**
     * Formats timestamp (ms) to full date time format (e.g. "dd MMM yyyy, HH:mm").
     */
    fun formatDateTimeString(timestampMs: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestampMs))
    }

    /**
     * Formats relative time elapsed from timestamp (e.g. "2d yang lalu").
     */
    fun formatRelativeTime(timestampMs: Long): String {
        val diffMs = System.currentTimeMillis() - timestampMs
        val seconds = diffMs / 1000
        return when {
            seconds < 5 -> "Baru saja"
            seconds < 60 -> "$seconds detik yang lalu"
            seconds < 3600 -> "${seconds / 60} menit yang lalu"
            else -> "${seconds / 3600} jam yang lalu"
        }
    }
}
