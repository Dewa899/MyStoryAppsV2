package com.submission.mystoryappsv2

import androidx.paging.PagingData
import com.submission.mystoryappsv2.data.pref.UserModel
import com.submission.mystoryappsv2.data.remote.LoginResponse
import com.submission.mystoryappsv2.data.remote.LoginResult
import com.submission.mystoryappsv2.data.remote.RegisterResponse
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.remote.StoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File

object DataDummy {
    fun generateToken() = "token"
    fun generateName() = "name"
    fun generateEmail() = "email@email.com"
    fun generatePassword() = "password"

    fun generateUserModel() = UserModel(
        email = generateEmail(),
        token = generateToken(),
        isLogin = true
    )

    fun generateFile() = File("file")
    fun generateDesc() = "desc"

    fun generateErrorResponse() = "error"

    fun generateLoginResponse() = LoginResponse(
         false, "success",
        LoginResult(
            generateName(),
            "id",
            generateToken()
        )
    )

    fun generateRegisterResponse() = RegisterResponse(false, "success")

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

    fun generateStoryResponse() = StoryResponse( false, "success",generateStoryList())

    /*fun generateAddResponse() = AddResponse(
        error = false,
        message = "success"
    )*/
    fun generateStoryPagingData(): Flow<PagingData<Story>> {
        val storyList = generateStoryList()
        return flowOf(PagingData.from(storyList))
    }

}