package com.ahimsarijalu.storyapp.data

import com.ahimsarijalu.storyapp.DataDummy
import com.ahimsarijalu.storyapp.data.remote.response.AuthResponse
import com.ahimsarijalu.storyapp.data.remote.response.FileUploadResponse
import com.ahimsarijalu.storyapp.data.remote.response.StoryResponse
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FakeApi : ApiService {
    override suspend fun getAllStories(token: String, page: Int, size: Int): StoryResponse {
        return DataDummy.generateDummyStoryResponse()
    }

    override suspend fun getAllStoriesWithLocation(token: String, location: Int): StoryResponse {
        return DataDummy.generateDummyStoryResponse()
    }


    override suspend fun registerUser(name: String, email: String, password: String): AuthResponse {
        return DataDummy.generateDummyRegister()
    }

    override suspend fun loginUser(email: String, password: String): AuthResponse {
        return DataDummy.generateDummyLogin()
    }

    override suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Float?,
        long: Float?
    ): FileUploadResponse {
        return DataDummy.generateDummyUpload()
    }
}