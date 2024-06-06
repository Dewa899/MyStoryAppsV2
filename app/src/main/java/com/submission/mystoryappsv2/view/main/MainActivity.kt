package com.submission.mystoryappsv2.view.main

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.pref.UserPreference
import com.submission.mystoryappsv2.data.pref.dataStore
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.view.ViewModelFactory
import com.submission.mystoryappsv2.view.addstory.AddStoryActivity
import com.submission.mystoryappsv2.view.login.LoginActivity
import com.submission.mystoryappsv2.view.story.StoryAdapter
import com.submission.mystoryappsv2.view.story.StoryDetailActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.core.app.ActivityCompat
import androidx.core.util.Pair


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addStoryButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var addStoryLauncher: ActivityResultLauncher<Intent>
    private var currentPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val logoutButton = findViewById<ExtendedFloatingActionButton>(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }
        recyclerView = findViewById(R.id.recycler_view)
        addStoryButton = findViewById(R.id.add_story_btn)
        progressBar = findViewById(R.id.progress_bar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StoryAdapter { story, view ->
            onItemClick(story,view)
        }


        recyclerView.adapter = adapter

        addStoryButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
        addStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadStories()
            }
        }


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadMoreStories()
                }
            }
        })
        loadStories()
    }
    private fun logout() {
        lifecycleScope.launch {
            try {
                viewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to logout", e)
                // Handle the exception, e.g., show an error message to the user
            }
        }
    }
    private fun loadStories() {
        val userPreference = UserPreference.getInstance(dataStore)
        currentPage = 1
        isLoading = true
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val userModel = userPreference.getSession().first()
                if (userModel.isLogin) { // Memeriksa status login sebelum memuat cerita
                    val token = userModel.token
                    viewModel.fetchStories("Bearer $token", currentPage)
                    viewModel.stories.observe(this@MainActivity) { stories ->
                        (recyclerView.adapter as StoryAdapter).submitList(stories)
                        isLoading = false
                        progressBar.visibility = View.GONE
                        recyclerView.smoothScrollToPosition(0)
                    }
                } else {
                    // Token kosong, arahkan kembali ke LoginActivity
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to load stories", e)
            }
        }
    }

    private fun loadMoreStories() {
        val userPreference = UserPreference.getInstance(dataStore)
        currentPage++
        isLoading = true
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            val token = userPreference.getSession().first().token
            if (token.isNotEmpty()) {
                viewModel.fetchStories("Bearer $token", currentPage)
                viewModel.stories.observe(this@MainActivity) { stories ->
                    val currentList = (recyclerView.adapter as StoryAdapter).currentList.toMutableList()
                    currentList.addAll(stories)
                    (recyclerView.adapter as StoryAdapter).submitList(currentList)
                    isLoading = false
                    progressBar.visibility = View.GONE
                }
            } else {
                isLoading = false
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadStories()
        startPolling()
    }

    override fun onPause() {
        super.onPause()
        stopPolling()
    }

    private fun startPolling() {
        handler.postDelayed(pollingRunnable, POLLING_INTERVAL)
    }

    private fun stopPolling() {
        handler.removeCallbacks(pollingRunnable)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val pollingRunnable = object : Runnable {
        override fun run() {
            loadStories()
            handler.postDelayed(this, POLLING_INTERVAL)
        }
    }
    private fun onItemClick(story: Story,itemView: View) {
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra("EXTRA_STORY_ID", story.id)
        }

        val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this@MainActivity,
            Pair(itemView.findViewById(R.id.iv_item_photo), "image"),
            Pair(itemView.findViewById(R.id.tv_item_name), "name")
        )

        ActivityCompat.startActivity(this@MainActivity, intent, optionsCompat.toBundle())
    }

    companion object {
        private const val POLLING_INTERVAL = 30000L // 1 minute in milliseconds (60000 milliseconds)
    }
}
