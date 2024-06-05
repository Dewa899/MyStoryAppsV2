package com.submission.mystoryappsv2.view.story

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.pref.dataStore
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.view.ViewModelFactory
import com.submission.mystoryappsv2.view.addstory.AddStoryActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoryListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addStoryButton: FloatingActionButton
    private val viewModel: StoryListViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        recyclerView = findViewById(R.id.recycler_view)
        addStoryButton = findViewById(R.id.add_story_btn)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter { story ->
            onItemClick(story)
        }
        recyclerView.adapter = adapter

        addStoryButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Load the stories into the RecyclerView here
        loadStories()
    }

    private fun loadStories() {
        val userPreference = UserPreference.getInstance(dataStore)

        lifecycleScope.launch {
            val token = userPreference.getSession().first().token
            if (token.isNotEmpty()) {
                viewModel.fetchStories("Bearer $token")
                viewModel.stories.observe(this@StoryListActivity) { stories ->
                    (recyclerView.adapter as StoryAdapter).submitList(stories)
                }
            } else {
                // Handle the case where the token is null or empty (e.g., prompt user to login)
            }
        }
    }

    private fun onItemClick(story: Story) {
        val intent = Intent(this, StoryDetailActivity::class.java)
        intent.putExtra("EXTRA_STORY", story)
        startActivity(intent)
    }
}
