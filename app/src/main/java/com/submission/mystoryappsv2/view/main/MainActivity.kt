package com.submission.mystoryappsv2.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import androidx.paging.LoadState
import com.submission.mystoryappsv2.data.adapter.StoryPagingAdapter
import com.submission.mystoryappsv2.view.maps.MapsActivity
import kotlinx.coroutines.flow.collectLatest


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var addStoryButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var addStoryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val logoutButton = findViewById<ExtendedFloatingActionButton>(R.id.logout_button)
        logoutButton.setOnClickListener {
            logout()
        }

        recyclerView = findViewById(R.id.recycler_view)
        addStoryButton = findViewById(R.id.add_story_btn)
        progressBar = findViewById(R.id.progress_bar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StoryPagingAdapter { story, view ->
            onItemClick(story, view)
        }

        recyclerView.adapter = adapter

        addStoryButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            addStoryLauncher.launch(intent)
        }

        addStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    viewModel.refreshStories()
                }
            }
        }

        addStoryButton.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            addStoryLauncher.launch(intent)
        }

        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            val userModel = userPreference.getSession().first()
            if (userModel.isLogin) {
                val token = userModel.token
                viewModel.getStoriesFlow("Bearer $token").collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        adapter.addLoadStateListener { loadState ->
            progressBar.visibility = if (loadState.refresh is LoadState.Loading) View.VISIBLE else View.GONE

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Log.e("MainActivity", "Paging error: ${it.error}")
            }
        }
    }
    /*override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val userPreference = UserPreference.getInstance(dataStore)
            val userModel = userPreference.getSession().first()
            if (userModel.isLogin) {
                val token = userModel.token
                viewModel.refreshStories()
                viewModel.getStoriesFlow("Bearer $token").collectLatest { pagingData ->
                    (recyclerView.adapter as? StoryPagingAdapter)?.submitData(pagingData)
                }
            } else {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            try {
                viewModel.logout()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to logout", e)
            }
        }
    }

    private fun onItemClick(story: Story, itemView: View) {
        val intent = Intent(this, StoryDetailActivity::class.java).apply {
            putExtra("EXTRA_STORY_ID", story.id)
        }

        val optionsCompat: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(
                this@MainActivity,
                Pair(itemView.findViewById(R.id.iv_item_photo), "image"),
                Pair(itemView.findViewById(R.id.tv_item_name), "name")
            )

        ActivityCompat.startActivity(this@MainActivity, intent, optionsCompat.toBundle())
    }

    companion object {
        private const val POLLING_INTERVAL = 30000L
    }
}