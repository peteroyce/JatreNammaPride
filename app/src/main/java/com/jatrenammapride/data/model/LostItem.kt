package com.jatrenammapride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lost_items")
data class LostItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,               // "Lost" or "Found"
    val description: String,
    val photoUrl: String,
    val contactInfo: String,
    val lastSeenLocation: String,
    val isResolved: Boolean = false,
    val firebaseKey: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
