package com.jatrenammapride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val location: String,
    val category: String,       // Religious, Cultural, Sports, Food
    val status: String,         // Upcoming, Ongoing, Completed
    val imageUrl: String? = null,
    val firebaseKey: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
