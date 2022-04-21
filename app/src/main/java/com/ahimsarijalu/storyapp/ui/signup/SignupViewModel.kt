package com.ahimsarijalu.storyapp.ui.signup

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ahimsarijalu.storyapp.data.remote.response.AuthResponse
import com.ahimsarijalu.storyapp.data.remote.retrofit.ApiConfig
import com.ahimsarijalu.storyapp.ui.login.LoginActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun signupUser(context: Context, name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().registerUser(name, email, password)
        client.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                    Intent(context, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(this)
                    }
                } else {
                    val message = response.errorBody()?.string()?.let {
                        JSONObject(it).getString("message")
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _isLoading.value = false
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }
}