package com.ahimsarijalu.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.ahimsarijalu.storyapp.data.remote.response.AuthResponse
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiService
import com.ahimsarijalu.storyapp.wrapEspressoIdlingResource

class AuthRepository(private val apiService: ApiService) {
    fun registerUser(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response =
                    apiService.registerUser(name, email, password)
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

    fun loginUser(
        email: String,
        password: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.loginUser(email, password)
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