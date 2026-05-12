package com.jatrenammapride.data.model

import org.junit.Assert.*
import org.junit.Test

class MapMarkerTest {
    @Test
    fun `marker types are valid`() {
        val validTypes = listOf("Parking", "FirstAid", "Stall", "Entry", "Exit")
        validTypes.forEach { type ->
            val marker = MapMarker(
                id = "test", label = "Test",
                type = type, latitude = 12.0, longitude = 77.0
            )
            assertTrue(validTypes.contains(marker.type))
        }
    }

    @Test
    fun `marker coordinates are in valid range`() {
        val marker = MapMarker(
            id = "test", label = "Test",
            type = "Entry", latitude = 12.9716, longitude = 77.5946
        )
        assertTrue(marker.latitude in -90.0..90.0)
        assertTrue(marker.longitude in -180.0..180.0)
    }

    @Test
    fun `marker description defaults to null`() {
        val marker = MapMarker(
            id = "test", label = "Test",
            type = "Parking", latitude = 12.0, longitude = 77.0
        )
        assertNull(marker.description)
    }

    @Test
    fun `marker with custom description`() {
        val marker = MapMarker(
            id = "test", label = "Test",
            type = "Stall", latitude = 12.0, longitude = 77.0,
            description = "Food stall near gate"
        )
        assertEquals("Food stall near gate", marker.description)
    }

    @Test
    fun `marker id and label are stored correctly`() {
        val marker = MapMarker(
            id = "marker-42", label = "Main Entrance",
            type = "Entry", latitude = 13.0, longitude = 78.0
        )
        assertEquals("marker-42", marker.id)
        assertEquals("Main Entrance", marker.label)
    }
}
