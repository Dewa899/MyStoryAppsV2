package com.submission.mystoryappsv2.data.repository

import StoryPagingSource
import android.util.Log
import com.submission.mystoryappsv2.data.pref.UserModel
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.remote.ApiResponse
import com.submission.mystoryappsv2.data.remote.ApiService
import com.submission.mystoryappsv2.data.remote.LoginResponse
import com.submission.mystoryappsv2.data.remote.RegisterResponse
import com.submission.mystoryappsv2.data.remote.Story
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun addStory(description: RequestBody, photo: MultipartBody.Part): ApiResponse {
        val token = userPreference.getSession().first().token
        return apiService.addStory("Bearer $token", description, photo, null, null)
    }

    fun getStories(token: String): Flow<PagingData<Story>> {
        return Pager(
            config = PagingConfig(pageSize = 5, enablePlaceholders = false),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }

    suspend fun getStoriesWithLocation(): List<Story> {
        val location = 1 // Set the location parameter here (1 for true, 0 for false)
        val token = userPreference.getSession().first().token
        return try {
            val response = apiService.getStories("Bearer $token", location = location)
            response.listStory // Assuming listStory is the list of stories from your API response
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Return an empty list in case of error
        }
    }

    suspend fun getStoryDetail(storyId: String): Story? {
        val token = userPreference.getSession().first().token
        Log.d("Repository", "Token: $token, Story ID: $storyId")

        if (token.isNotEmpty()) {
            try {
                val response = apiService.getStoryDetail("Bearer $token", storyId)
                logResponseDetails(response)

                if (response.isSuccessful) {
                    Log.d("Repository", "Response: ${response.body()}")
                    return response.body()?.story
                } else {
                    Log.e("Repository", "Error response code: ${response.code()}")
                    Log.e("Repository", "Error response message: ${response.message()}")
                    Log.e("Repository", "Error response body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Repository", "Exception during API call", e)
            }
        } else {
            Log.e("Repository", "Token is empty")
        }
        return null
    }

    private fun logResponseDetails(response: Response<*>) {
        Log.d("Repository", "Response URL: ${response.raw().request.url}")
        Log.d("Repository", "Response headers: ${response.headers()}")
        Log.d("Repository", "Response body: ${response.body()}")
        if (!response.isSuccessful) {
            Log.e("Repository", "Error response: ${response.errorBody()?.string()}")
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
