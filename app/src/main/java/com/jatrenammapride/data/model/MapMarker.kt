package com.jatrenammapride.data.model

data class MapMarker(
    val id: String,
    val label: String,
    val type: String,           // Parking, FirstAid, Stall, Entry, Exit
    val latitude: Double,
    val longitude: Double,
    val description: String? = null
)
