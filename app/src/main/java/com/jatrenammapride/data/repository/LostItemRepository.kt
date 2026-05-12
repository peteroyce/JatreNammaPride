package com.jatrenammapride.data.repository

import android.net.Uri
import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.LostItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class LostItemRepository(
    private val database: AppDatabase
) {
    private val lostItemDao = database.lostItemDao()

    fun getActiveItems(): Flow<List<LostItem>> {
        return lostItemDao.getActiveItems()
    }

    fun getAllItems(): Flow<List<LostItem>> {
        return lostItemDao.getAllItems()
    }

    /**
     * If the local database has no lost items, preload sample data.
     */
    suspend fun preloadSampleItemsIfEmpty() {
        val existing = lostItemDao.getAllItems().first()
        if (existing.isNotEmpty()) return

        val sampleItems = listOf(
            LostItem(
                type = "Lost",
                description = "Brown leather wallet with ID cards and some cash. Lost near the food stalls area around noon.",
                photoUrl = "",
                contactInfo = "9876543210",
                lastSeenLocation = "Food Stalls Area"
            ),
            LostItem(
                type = "Found",
                description = "Black Samsung phone found on a bench near the main stage. Screen lock is on.",
                photoUrl = "",
                contactInfo = "9123456780",
                lastSeenLocation = "Main Stage"
            )
        )
        for (item in sampleItems) {
            lostItemDao.insertItem(item)
        }
    }

    suspend fun postItem(item: LostItem) {
        lostItemDao.insertItem(item)
    }

    suspend fun markResolved(item: LostItem) {
        val resolved = item.copy(isResolved = true)
        lostItemDao.updateItem(resolved)
    }

    @Suppress("UNUSED_PARAMETER")
    suspend fun uploadImage(uri: Uri): String {
        // Return a placeholder URL instead of uploading to Firebase Storage
        return "https://placeholder.example.com/images/${UUID.randomUUID()}.jpg"
    }
}
