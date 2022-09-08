package com.ahimsarijalu.storyapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ahimsarijalu.storyapp.data.local.model.User
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.data.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, storyRepository: StoryRepository) :
    ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> = Transformations.switchMap(getUser()) {
        storyRepository.getStory(it.token).cachedIn(viewModelScope)
    }

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}