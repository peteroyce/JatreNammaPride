package com.jatrenammapride.data.repository

import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EventRepository(
    private val database: AppDatabase
) {
    private val eventDao = database.eventDao()

    fun getEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents()
    }

    fun getEventsByCategory(category: String): Flow<List<Event>> {
        return eventDao.getEventsByCategory(category)
    }

    /**
     * If the local database is empty, preload sample Jatre events.
     */
    suspend fun preloadSampleEventsIfEmpty() {
        val existing = eventDao.getAllEvents().first()
        if (existing.isNotEmpty()) return

        val now = System.currentTimeMillis()
        val hour = 3_600_000L
        val sampleEvents = listOf(
            Event(
                title = "Rathotsava Procession",
                description = "The grand chariot procession through the main streets. Devotees pull the towering ratha adorned with flowers and lights.",
                startTime = now + 2 * hour,
                endTime = now + 5 * hour,
                location = "Temple Main Road",
                category = "Religious",
                status = "Upcoming"
            ),
            Event(
                title = "Wrestling Championship",
                description = "Traditional kusti matches featuring wrestlers from across the region competing for honour and glory.",
                startTime = now + 6 * hour,
                endTime = now + 9 * hour,
                location = "Central Arena",
                category = "Sports",
                status = "Upcoming"
            ),
            Event(
                title = "Folk Dance Performances",
                description = "Vibrant Yakshagana and Dollu Kunitha performances by renowned troupes from across Karnataka.",
                startTime = now + 10 * hour,
                endTime = now + 13 * hour,
                location = "Main Stage",
                category = "Cultural",
                status = "Upcoming"
            ),
            Event(
                title = "Food Festival",
                description = "Savour local delicacies — mirchi bajji, jolada rotti, karadantu, cotton candy, and more from 30+ stalls.",
                startTime = now + 1 * hour,
                endTime = now + 14 * hour,
                location = "Food Stalls Area",
                category = "Food",
                status = "Ongoing"
            ),
            Event(
                title = "Closing Ceremony & Lamp Lighting",
                description = "Hundreds of oil lamps illuminate the temple pathway as the jatre concludes with prayers and community gathering.",
                startTime = now + 24 * hour,
                endTime = now + 27 * hour,
                location = "Temple Pond",
                category = "Religious",
                status = "Upcoming"
            )
        )
        for (event in sampleEvents) {
            eventDao.insertEvent(event)
        }
    }

    suspend fun addEvent(event: Event) {
        eventDao.insertEvent(event)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event)
    }
}
