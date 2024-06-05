package com.submission.mystoryappsv2.data.story

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val userName: String,
    val photoUrl: String,
    val description: String
) : Parcelable
