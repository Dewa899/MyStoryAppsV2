package com.submission.mystoryappsv2.data.repository

import com.submission.mystoryappsv2.data.pref.UserModel
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.remote.ApiResponse
import com.submission.mystoryappsv2.data.remote.ApiService
import com.submission.mystoryappsv2.data.remote.LoginResponse
import com.submission.mystoryappsv2.data.remote.RegisterResponse
import com.submission.mystoryappsv2.data.remote.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    // User-related methods
    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    // Story-related methods
    suspend fun addStory(description: RequestBody, photo: MultipartBody.Part): ApiResponse {
        val token = userPreference.getSession().first().token
        return apiService.addStory("Bearer $token", description, photo)
    }

    suspend fun getStories(token: String, page: Int? = null, size: Int? = null, location: Int? = 0): List<Story> {
        val response = apiService.getStories(token, page, size, location)
        if (!response.error) {
            return response.listStory
        } else {
            throw Exception(response.message)
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService, userPreference).also { instance = it }
            }
    }
}
