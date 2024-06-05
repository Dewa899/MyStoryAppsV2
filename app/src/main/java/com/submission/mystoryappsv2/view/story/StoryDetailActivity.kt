package com.submission.mystoryappsv2.view.story

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.story.Story

class StoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        val story = intent.getParcelableExtra<Story>("EXTRA_STORY")

        val nameTextView: TextView = findViewById(R.id.tv_detail_name)
        val photoImageView: ImageView = findViewById(R.id.iv_detail_photo)
        val descriptionTextView: TextView = findViewById(R.id.tv_detail_description)

        nameTextView.text = story?.userName
        Glide.with(this)
            .load(story?.photoUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(photoImageView)
        descriptionTextView.text = story?.description
    }
}
