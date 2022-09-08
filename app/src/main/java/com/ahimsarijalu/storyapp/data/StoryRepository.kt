package com.ahimsarijalu.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.ahimsarijalu.storyapp.data.local.StoryRemoteMediator
import com.ahimsarijalu.storyapp.data.local.database.StoryDatabase
import com.ahimsarijalu.storyapp.data.remote.response.FileUploadResponse
import com.ahimsarijalu.storyapp.data.remote.response.ListStoryItem
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiConfig
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiService
import com.ahimsarijalu.storyapp.reduceFileImage
import com.ahimsarijalu.storyapp.wrapEspressoIdlingResource
import com.google.android.gms.maps.model.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStoryFromLocal()
            }
        ).liveData
    }

    fun getStoryWithLocation(
        token: String
    ): LiveData<Result<List<ListStoryItem>>> = liveData {
        wrapEspressoIdlingResource {
            try {
                val response =
                    ApiConfig.getApiService().getAllStoriesWithLocation("Bearer $token", location = 1)
                if (!response.error) {
                    emit(Result.Success(response.listStory))
                } else {
                    emit(Result.Error(response.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    }

    fun addStory(
        getFile: File?,
        description: String,
        token: String,
        location: LatLng?
    ): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val descriptionAsRequestBody = description.toRequestBody("text/plain".toMediaType())
                val lat = location?.latitude?.toFloat()
                val lon = location?.longitude?.toFloat()

                val file = reduceFileImage(getFile!!)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                val response = ApiConfig.getApiService().uploadImage(
                    "Bearer $token",
                    imageMultiPart,
                    descriptionAsRequestBody,
                    lat,
                    lon
                )

                if (!response.error) {
                    emit(Result.Success(response))
                } else {
                    emit(Result.Error(response.message))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    }

}