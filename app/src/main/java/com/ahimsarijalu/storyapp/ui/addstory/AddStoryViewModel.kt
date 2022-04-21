package com.ahimsarijalu.storyapp.ui.addstory

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ahimsarijalu.storyapp.data.model.User
import com.ahimsarijalu.storyapp.data.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.FileUploadResponse
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiConfig
import com.ahimsarijalu.storyapp.reduceFileImage
import com.ahimsarijalu.storyapp.ui.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: UserPreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun uploadImage(context: Context, getFile: File?, description: String, token: String) {
        _isLoading.value = true

        val descriptionAsRequestBody = description.toRequestBody("text/plain".toMediaType())
        val file = reduceFileImage(getFile!!)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val service = ApiConfig.getApiService()
            .uploadImage("Bearer $token", imageMultiPart, descriptionAsRequestBody)
        service.enqueue(object : Callback<FileUploadResponse> {
            override fun onResponse(
                call: Call<FileUploadResponse>,
                response: Response<FileUploadResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Toast.makeText(context, responseBody.message, Toast.LENGTH_SHORT).show()
                    }

                    Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(this)
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
                _isLoading.value = false
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}