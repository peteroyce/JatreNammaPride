package com.jatrenammapride.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    /**
     * Formats milliseconds to time string like "4:00 PM"
     */
    fun formatTime(millis: Long): String {
        return timeFormat.format(Date(millis))
    }

    /**
     * Formats milliseconds to date string like "24 Apr 2026"
     */
    fun formatDate(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    /**
     * Formats a time range like "4:00 PM - 6:00 PM"
     */
    fun formatTimeRange(start: Long, end: Long): String {
        return "${formatTime(start)} - ${formatTime(end)}"
    }

    /**
     * Returns a relative time string like "2 hours ago", "just now", "3 days ago", etc.
     */
    fun getRelativeTime(millis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - millis

        if (diff < 0) {
            return "just now"
        }

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "just now"
            minutes < 2 -> "1 minute ago"
            minutes < 60 -> "$minutes minutes ago"
            hours < 2 -> "1 hour ago"
            hours < 24 -> "$hours hours ago"
            days < 2 -> "1 day ago"
            days < 30 -> "$days days ago"
            days < 60 -> "1 month ago"
            days < 365 -> "${days / 30} months ago"
            days < 730 -> "1 year ago"
            else -> "${days / 365} years ago"
        }
    }
}
