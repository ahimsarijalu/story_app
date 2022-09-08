package com.ahimsarijalu.storyapp.ui.signup

import androidx.lifecycle.ViewModel
import com.ahimsarijalu.storyapp.data.AuthRepository

class SignupViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun signupUser(name: String, email: String, password: String) = authRepository.registerUser(name, email, password)
}