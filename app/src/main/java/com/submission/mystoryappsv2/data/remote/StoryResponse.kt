package com.submission.mystoryappsv2.data.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)

@Parcelize
data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double? = null,
    val lon: Double? = null
): Parcelable

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)