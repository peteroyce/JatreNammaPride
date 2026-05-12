package com.jatrenammapride.ui.lostfound

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.LostItem
import com.jatrenammapride.data.repository.LostItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LostFoundViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LostItemRepository

    private val _items = MutableStateFlow<List<LostItem>>(emptyList())
    val items: StateFlow<List<LostItem>> = _items.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _contactInfo = MutableStateFlow("")
    val contactInfo: StateFlow<String> = _contactInfo.asStateFlow()

    private val _lastSeenLocation = MutableStateFlow("")
    val lastSeenLocation: StateFlow<String> = _lastSeenLocation.asStateFlow()

    private val _type = MutableStateFlow("Lost")
    val type: StateFlow<String> = _type.asStateFlow()

    private val _uploadedImageUrl = MutableStateFlow<String?>(null)
    val uploadedImageUrl: StateFlow<String?> = _uploadedImageUrl.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = LostItemRepository(db)
        viewModelScope.launch {
            repository.preloadSampleItemsIfEmpty()
        }
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            repository.getActiveItems().collectLatest { itemList ->
                _items.value = itemList
            }
        }
    }

    fun updateDescription(value: String) {
        _description.value = value
    }

    fun updateContactInfo(value: String) {
        _contactInfo.value = value
    }

    fun updateLastSeenLocation(value: String) {
        _lastSeenLocation.value = value
    }

    fun updateType(value: String) {
        _type.value = value
    }

    fun postItem() {
        viewModelScope.launch {
            val item = LostItem(
                type = _type.value,
                description = _description.value,
                photoUrl = _uploadedImageUrl.value ?: "",
                contactInfo = _contactInfo.value,
                lastSeenLocation = _lastSeenLocation.value
            )
            repository.postItem(item)
            // Clear form
            _description.value = ""
            _contactInfo.value = ""
            _lastSeenLocation.value = ""
            _type.value = "Lost"
            _uploadedImageUrl.value = null
        }
    }

    fun markResolved(item: LostItem) {
        viewModelScope.launch {
            repository.markResolved(item)
        }
    }

    fun setImageUri(uri: Uri?) {
        _uploadedImageUrl.value = uri?.toString()
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            try {
                val url = repository.uploadImage(uri)
                _uploadedImageUrl.value = url
            } catch (_: Exception) {
                // Upload failed - fall back to storing the local URI
                _uploadedImageUrl.value = uri.toString()
            }
        }
    }
}
