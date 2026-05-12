package com.jatrenammapride.ui.map

import androidx.lifecycle.ViewModel
import com.jatrenammapride.data.model.MapMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val allMarkers = listOf(
        MapMarker(
            id = "parking_a",
            label = "Parking Zone A",
            type = "Parking",
            latitude = 12.9716,
            longitude = 77.5946,
            description = "Main parking area near the south entrance. Capacity: 200 vehicles."
        ),
        MapMarker(
            id = "parking_b",
            label = "Parking Zone B",
            type = "Parking",
            latitude = 12.9720,
            longitude = 77.5950,
            description = "Overflow parking near the north gate. Two-wheeler priority zone."
        ),
        MapMarker(
            id = "first_aid",
            label = "First Aid Post",
            type = "FirstAid",
            latitude = 12.9718,
            longitude = 77.5948,
            description = "24-hour medical assistance with trained paramedics and ambulance on standby."
        ),
        MapMarker(
            id = "food_stalls",
            label = "Food Stalls Area",
            type = "Stall",
            latitude = 12.9714,
            longitude = 77.5944,
            description = "Over 30 food stalls offering local delicacies, snacks, and beverages."
        ),
        MapMarker(
            id = "main_stage",
            label = "Main Stage",
            type = "Stall",
            latitude = 12.9719,
            longitude = 77.5947,
            description = "Central performance stage for cultural events, music, and announcements."
        ),
        MapMarker(
            id = "entry_gate",
            label = "Entry Gate",
            type = "Entry",
            latitude = 12.9712,
            longitude = 77.5942,
            description = "Main entry point with security check and ticket counter."
        ),
        MapMarker(
            id = "exit_gate",
            label = "Exit Gate",
            type = "Exit",
            latitude = 12.9722,
            longitude = 77.5952,
            description = "Main exit with auto and taxi stand nearby."
        )
    )

    private val _allMarkersFlow = MutableStateFlow(allMarkers)
    val allMarkersFlow: StateFlow<List<MapMarker>> = _allMarkersFlow.asStateFlow()

    private val _markers = MutableStateFlow(allMarkers)
    val markers: StateFlow<List<MapMarker>> = _markers.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun selectFilter(filter: String) {
        _selectedFilter.value = filter
        _markers.value = if (filter == "All") {
            allMarkers
        } else {
            allMarkers.filter { it.type == filter }
        }
    }
}
