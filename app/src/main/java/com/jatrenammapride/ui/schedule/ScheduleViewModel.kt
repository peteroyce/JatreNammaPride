package com.jatrenammapride.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.Event
import com.jatrenammapride.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EventRepository

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = EventRepository(db)
        viewModelScope.launch {
            repository.preloadSampleEventsIfEmpty()
        }
        loadEvents()
    }

    private fun loadEvents() {
        viewModelScope.launch {
            repository.getEvents().collectLatest { eventList ->
                _events.value = if (_selectedCategory.value == "All") {
                    eventList
                } else {
                    eventList.filter { it.category == _selectedCategory.value }
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        loadEvents()
    }

    fun refreshEvents() {
        // No remote sync needed — data is local only
    }
}
