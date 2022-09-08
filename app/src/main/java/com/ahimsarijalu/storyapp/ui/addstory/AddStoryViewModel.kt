package com.ahimsarijalu.storyapp.ui.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ahimsarijalu.storyapp.data.StoryRepository
import com.ahimsarijalu.storyapp.data.local.model.User
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.google.android.gms.maps.model.LatLng
import java.io.File

class AddStoryViewModel(
    private val pref: UserPreference? = null,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getUser(): LiveData<User> {
        return pref!!.getUser().asLiveData()
    }

    fun uploadImage(
        getFile: File?,
        description: String,
        token: String,
        location: LatLng?
    ) = storyRepository.addStory(
        getFile,
        description,
        token,
        location
    )
}