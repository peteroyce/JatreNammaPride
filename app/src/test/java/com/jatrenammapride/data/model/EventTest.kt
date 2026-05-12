package com.jatrenammapride.data.model

import org.junit.Assert.*
import org.junit.Test

class EventTest {
    @Test
    fun `event creation with default values`() {
        val event = Event(
            title = "Test Event",
            description = "Description",
            startTime = 1000L,
            endTime = 2000L,
            location = "Test Location",
            category = "Cultural",
            status = "Upcoming"
        )
        assertEquals("Test Event", event.title)
        assertEquals("Cultural", event.category)
        assertEquals("Upcoming", event.status)
        assertNull(event.imageUrl)
        assertNull(event.firebaseKey)
        assertEquals(0, event.id)
    }

    @Test
    fun `event status values are valid`() {
        val validStatuses = listOf("Upcoming", "Ongoing", "Completed")
        validStatuses.forEach { status ->
            val event = Event(
                title = "E", description = "D",
                startTime = 0, endTime = 0,
                location = "L", category = "Cultural",
                status = status
            )
            assertTrue(validStatuses.contains(event.status))
        }
    }

    @Test
    fun `event categories are valid`() {
        val validCategories = listOf("Religious", "Cultural", "Sports", "Food")
        validCategories.forEach { cat ->
            val event = Event(
                title = "E", description = "D",
                startTime = 0, endTime = 0,
                location = "L", category = cat,
                status = "Upcoming"
            )
            assertTrue(validCategories.contains(event.category))
        }
    }

    @Test
    fun `event with custom imageUrl and firebaseKey`() {
        val event = Event(
            title = "E", description = "D",
            startTime = 0, endTime = 0,
            location = "L", category = "Cultural",
            status = "Upcoming",
            imageUrl = "https://example.com/img.jpg",
            firebaseKey = "abc123"
        )
        assertEquals("https://example.com/img.jpg", event.imageUrl)
        assertEquals("abc123", event.firebaseKey)
    }

    @Test
    fun `event time range is valid`() {
        val event = Event(
            title = "E", description = "D",
            startTime = 1000L, endTime = 2000L,
            location = "L", category = "Cultural",
            status = "Upcoming"
        )
        assertTrue(event.endTime > event.startTime)
    }
}
