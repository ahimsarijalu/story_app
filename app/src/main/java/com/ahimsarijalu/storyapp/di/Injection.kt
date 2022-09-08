package com.ahimsarijalu.storyapp.di

import android.content.Context
import com.ahimsarijalu.storyapp.data.AuthRepository
import com.ahimsarijalu.storyapp.data.StoryRepository
import com.ahimsarijalu.storyapp.data.local.database.StoryDatabase
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }

    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService)
    }
}