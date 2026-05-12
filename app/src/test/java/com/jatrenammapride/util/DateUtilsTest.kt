package com.jatrenammapride.util

import org.junit.Assert.*
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `formatTime returns non-empty string`() {
        val result = DateUtils.formatTime(System.currentTimeMillis())
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatTime contains AM or PM`() {
        val result = DateUtils.formatTime(System.currentTimeMillis())
        assertTrue(result.contains("AM") || result.contains("PM"))
    }

    @Test
    fun `formatDate returns non-empty string`() {
        val result = DateUtils.formatDate(System.currentTimeMillis())
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatDate contains year`() {
        val result = DateUtils.formatDate(System.currentTimeMillis())
        // The formatted date should contain a 4-digit year
        assertTrue(result.matches(Regex(".*\\d{4}.*")))
    }

    @Test
    fun `formatTimeRange includes both times`() {
        val start = System.currentTimeMillis()
        val end = start + 2 * 60 * 60 * 1000 // 2 hours later

        val result = DateUtils.formatTimeRange(start, end)
        assertTrue(result.contains(" - "))
        // Should contain two AM/PM markers
        val parts = result.split(" - ")
        assertEquals(2, parts.size)
        assertTrue(parts[0].isNotEmpty())
        assertTrue(parts[1].isNotEmpty())
    }

    @Test
    fun `getRelativeTime returns just now for recent time`() {
        val result = DateUtils.getRelativeTime(System.currentTimeMillis())
        assertEquals("just now", result)
    }

    @Test
    fun `getRelativeTime returns just now for future time`() {
        val futureTime = System.currentTimeMillis() + 60_000
        val result = DateUtils.getRelativeTime(futureTime)
        assertEquals("just now", result)
    }

    @Test
    fun `getRelativeTime returns minutes ago`() {
        val fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000
        val result = DateUtils.getRelativeTime(fiveMinutesAgo)
        assertTrue(result.contains("minutes ago"))
    }

    @Test
    fun `getRelativeTime returns hours ago`() {
        val threeHoursAgo = System.currentTimeMillis() - 3 * 60 * 60 * 1000
        val result = DateUtils.getRelativeTime(threeHoursAgo)
        assertTrue(result.contains("hours ago"))
    }

    @Test
    fun `getRelativeTime returns days ago`() {
        val fiveDaysAgo = System.currentTimeMillis() - 5L * 24 * 60 * 60 * 1000
        val result = DateUtils.getRelativeTime(fiveDaysAgo)
        assertTrue(result.contains("days ago"))
    }

    @Test
    fun `formatTime for epoch zero returns non-empty string`() {
        val result = DateUtils.formatTime(0L)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `formatDate for epoch zero returns non-empty string`() {
        val result = DateUtils.formatDate(0L)
        assertTrue(result.isNotEmpty())
    }
}
