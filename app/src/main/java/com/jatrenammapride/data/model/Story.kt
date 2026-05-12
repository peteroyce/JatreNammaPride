package com.jatrenammapride.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class Story(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val jatreName: String,
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
