package com.jatrenammapride.data.model

import org.junit.Assert.*
import org.junit.Test

class LostItemTest {
    @Test
    fun `lost item defaults to unresolved`() {
        val item = LostItem(
            type = "Lost",
            description = "Wallet",
            photoUrl = "",
            contactInfo = "123",
            lastSeenLocation = "Stage"
        )
        assertFalse(item.isResolved)
    }

    @Test
    fun `lost item type is Lost or Found`() {
        val lost = LostItem(type = "Lost", description = "D", photoUrl = "", contactInfo = "C", lastSeenLocation = "L")
        val found = LostItem(type = "Found", description = "D", photoUrl = "", contactInfo = "C", lastSeenLocation = "L")
        assertEquals("Lost", lost.type)
        assertEquals("Found", found.type)
    }

    @Test
    fun `lost item default id is zero`() {
        val item = LostItem(type = "Lost", description = "D", photoUrl = "", contactInfo = "C", lastSeenLocation = "L")
        assertEquals(0, item.id)
    }

    @Test
    fun `lost item firebaseKey defaults to null`() {
        val item = LostItem(type = "Lost", description = "D", photoUrl = "", contactInfo = "C", lastSeenLocation = "L")
        assertNull(item.firebaseKey)
    }

    @Test
    fun `lost item can be marked resolved`() {
        val item = LostItem(
            type = "Lost", description = "D", photoUrl = "",
            contactInfo = "C", lastSeenLocation = "L",
            isResolved = true
        )
        assertTrue(item.isResolved)
    }
}
