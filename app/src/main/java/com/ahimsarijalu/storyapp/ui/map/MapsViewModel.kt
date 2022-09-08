package com.ahimsarijalu.storyapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ahimsarijalu.storyapp.data.StoryRepository
import com.ahimsarijalu.storyapp.data.local.model.User
import com.ahimsarijalu.storyapp.data.local.model.UserPreference

class MapsViewModel(
    private val pref: UserPreference? = null,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getUser(): LiveData<User> {
        return pref!!.getUser().asLiveData()
    }

    fun getStoryWithLocation(
        token: String
    ) = storyRepository.getStoryWithLocation(token)
}