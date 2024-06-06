package com.submission.mystoryappsv2.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.mystoryappsv2.data.pref.UserModel
import com.submission.mystoryappsv2.data.repository.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (!response.error) {
                    val user = UserModel(
                        email = email,
                        token = response.loginResult.token,
                        isLogin = true
                    )
                    repository.saveSession(user)
                    onResult(true, "Login success")
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "An error occurred")
            }
        }
    }
}