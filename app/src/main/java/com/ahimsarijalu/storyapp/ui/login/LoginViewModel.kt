package com.ahimsarijalu.storyapp.ui.login

import androidx.lifecycle.*
import com.ahimsarijalu.storyapp.data.AuthRepository
import com.ahimsarijalu.storyapp.data.local.model.User
import com.ahimsarijalu.storyapp.data.local.model.UserPreference
import com.ahimsarijalu.storyapp.data.remote.response.LoginResult
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference? = null, private val authRepository: AuthRepository) : ViewModel() {
    fun getUser(): LiveData<User> {
        return pref!!.getUser().asLiveData()
    }

    fun saveUser(user: LoginResult) {
        viewModelScope.launch {
            pref!!.saveUser(user)
        }
    }

    fun loginUser(email: String, password: String) = authRepository.loginUser(email, password)
}