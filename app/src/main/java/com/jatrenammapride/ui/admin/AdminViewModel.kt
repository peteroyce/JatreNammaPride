package com.jatrenammapride.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.Event
import com.jatrenammapride.data.model.LostItem
import com.jatrenammapride.data.repository.EventRepository
import com.jatrenammapride.data.repository.LostItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository: EventRepository
    private val lostItemRepository: LostItemRepository

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    val lostItems: StateFlow<List<LostItem>> = _lostItems.asStateFlow()

    // Form state for adding events
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _category = MutableStateFlow("Religious")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow("")
    val endTime: StateFlow<String> = _endTime.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        eventRepository = EventRepository(db)
        lostItemRepository = LostItemRepository(db)
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            eventRepository.getEvents().collectLatest { eventList ->
                _events.value = eventList
            }
        }
        viewModelScope.launch {
            lostItemRepository.getAllItems().collectLatest { itemList ->
                _lostItems.value = itemList
            }
        }
    }

    fun updateTitle(value: String) { _title.value = value }
    fun updateDescription(value: String) { _description.value = value }
    fun updateLocation(value: String) { _location.value = value }
    fun updateCategory(value: String) { _category.value = value }
    fun updateStartTime(value: String) { _startTime.value = value }
    fun updateEndTime(value: String) { _endTime.value = value }
    fun toggleAddDialog() { _showAddDialog.value = !_showAddDialog.value }

    private fun parseDateTimeToMillis(input: String, fallback: Long): Long {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return fallback
        // Try parsing "YYYY-MM-DD HH:MM" format
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            formatter.parse(trimmed)?.time ?: fallback
        } catch (_: Exception) {
            // Fallback: try parsing as raw epoch millis for backward compatibility
            try {
                trimmed.toLong()
            } catch (_: NumberFormatException) {
                fallback
            }
        }
    }

    fun addEvent() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val startMillis = parseDateTimeToMillis(_startTime.value, now)
            val endMillis = parseDateTimeToMillis(_endTime.value, startMillis + 3600000L)

            val event = Event(
                title = _title.value,
                description = _description.value,
                location = _location.value,
                category = _category.value,
                startTime = startMillis,
                endTime = endMillis,
                status = "Upcoming"
            )
            eventRepository.addEvent(event)
            clearForm()
            _showAddDialog.value = false
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.deleteEvent(event)
        }
    }

    fun deleteLostItem(item: LostItem) {
        viewModelScope.launch {
            val db = AppDatabase.getInstance(getApplication())
            db.lostItemDao().deleteItem(item)
        }
    }

    private fun clearForm() {
        _title.value = ""
        _description.value = ""
        _location.value = ""
        _category.value = "Religious"
        _startTime.value = ""
        _endTime.value = ""
    }
}
