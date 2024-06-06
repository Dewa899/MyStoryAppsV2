package com.submission.mystoryappsv2.view.story

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.data.repository.Repository
import com.submission.mystoryappsv2.di.Injection
import kotlinx.coroutines.launch


class StoryDetailActivity : AppCompatActivity() {
    private lateinit var repository: Repository
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        repository = Injection.provideRepository(this)
        progressBar = findViewById(R.id.progress_bar)

        val storyId = intent.getStringExtra("EXTRA_STORY_ID")
        Log.d("StoryDetailActivity", "Story ID: $storyId")

        if (storyId != null) {
            fetchStoryDetail(storyId)
        } else {
            Log.e("StoryDetailActivity", "Story ID is null")
        }
    }

    private fun fetchStoryDetail(storyId: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val story = repository.getStoryDetail(storyId)
                if (story != null) {
                    Log.d("StoryDetailActivity", "Story fetched: $story")
                    updateUI(story)
                } else {
                    Log.e("StoryDetailActivity", "Story is null")
                }
            } catch (e: Exception) {
                Log.e("StoryDetailActivity", "Error fetching story", e)
            }finally {
                
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun updateUI(story: Story) {
        val nameTextView: TextView = findViewById(R.id.tv_detail_name)
        val photoImageView: ImageView = findViewById(R.id.iv_detail_photo)
        val descriptionTextView: TextView = findViewById(R.id.tv_detail_description)

        nameTextView.text = story.name
        Glide.with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.image_placeholder)
            .into(photoImageView)
        descriptionTextView.text = story.description
    }
}
