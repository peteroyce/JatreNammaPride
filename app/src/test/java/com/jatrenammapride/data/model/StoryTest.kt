package com.jatrenammapride.data.model

import org.junit.Assert.*
import org.junit.Test

class StoryTest {
    @Test
    fun `story creation with required fields`() {
        val story = Story(
            title = "Temple History",
            content = "A long and rich history...",
            jatreName = "Dasara"
        )
        assertEquals("Temple History", story.title)
        assertEquals("A long and rich history...", story.content)
        assertEquals("Dasara", story.jatreName)
    }

    @Test
    fun `story default id is zero`() {
        val story = Story(title = "T", content = "C", jatreName = "J")
        assertEquals(0, story.id)
    }

    @Test
    fun `story imageUrl defaults to null`() {
        val story = Story(title = "T", content = "C", jatreName = "J")
        assertNull(story.imageUrl)
    }

    @Test
    fun `story with custom imageUrl`() {
        val story = Story(
            title = "T", content = "C", jatreName = "J",
            imageUrl = "https://example.com/story.jpg"
        )
        assertEquals("https://example.com/story.jpg", story.imageUrl)
    }

    @Test
    fun `story createdAt is set automatically`() {
        val before = System.currentTimeMillis()
        val story = Story(title = "T", content = "C", jatreName = "J")
        val after = System.currentTimeMillis()
        assertTrue(story.createdAt in before..after)
    }
}
