package com.jatrenammapride.ui.stories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jatrenammapride.data.local.AppDatabase
import com.jatrenammapride.data.model.Story
import com.jatrenammapride.data.repository.StoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StoryRepository

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories.asStateFlow()

    init {
        val db = AppDatabase.getInstance(application)
        repository = StoryRepository(db)
        preloadAndObserve()
    }

    private fun preloadAndObserve() {
        viewModelScope.launch {
            repository.preloadSampleStoriesIfEmpty()

            // Now observe
            repository.getStories().collectLatest { storyList ->
                _stories.value = storyList
            }
        }
    }
}
