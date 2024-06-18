package com.submission.mystoryappsv2

import androidx.paging.PagingData
import com.submission.mystoryappsv2.data.pref.UserModel
import com.submission.mystoryappsv2.data.remote.LoginResponse
import com.submission.mystoryappsv2.data.remote.LoginResult
import com.submission.mystoryappsv2.data.remote.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object DataDummy {
    fun generateToken() = "token"
    private fun generateName() = "name"
    private fun generateEmail() = "email@email.com"

    fun generateUserModel() = UserModel(
        email = generateEmail(),
        token = generateToken(),
        isLogin = true
    )

    fun generateLoginResponse() = LoginResponse(
         false, "success",
        LoginResult(
            generateName(),
            "id",
            generateToken()
        )
    )

    fun generateStoryList() = List(6) { index ->
        Story(
            id = "id_$index",
            photoUrl = "photoUrl_$index",
            createdAt = "createdAt_$index",
            name = generateName(),
            description = "description_$index",
            lon = 0.0,
            lat = 0.0
        )
    }

    fun generateStoryPagingData(): Flow<PagingData<Story>> {
        val storyList = generateStoryList()
        return flowOf(PagingData.from(storyList))
    }

}