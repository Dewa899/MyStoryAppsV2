package com.submission.mystoryappsv2.view.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: Repository) : ViewModel() {
    fun register(name: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("SignupViewModel", "Registering user with name: $name, email: $email")
                val response = repository.register(name, email, password)
                Log.d("SignupViewModel", "Register response: ${response.message}")
                onResult(!response.error, response.message)
            } catch (e: Exception) {
                Log.e("SignupViewModel", "Error during registration", e)
                onResult(false, e.message ?: "An error occurred")
            }
        }
    }
}
