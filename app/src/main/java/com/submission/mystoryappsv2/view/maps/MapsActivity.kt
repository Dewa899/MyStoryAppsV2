package com.submission.mystoryappsv2.view.maps


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.submission.mystoryappsv2.R
import com.submission.mystoryappsv2.data.remote.Story
import com.submission.mystoryappsv2.databinding.ActivityMapsBinding
import com.submission.mystoryappsv2.view.ViewModelFactory
import com.submission.mystoryappsv2.view.maps.MapsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MapsViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeStoryResponse()
    }

    private fun observeStoryResponse() {
        lifecycleScope.launch {
            val stories = viewModel.getStoriesWithLocation()
            displayStoriesOnMap(stories)
        }
    }

    private fun displayStoriesOnMap(stories: List<Story>) {
        mMap.clear() // Clear existing markers
        stories.forEach { story ->
            val latLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name ?: "")
                    .snippet(story.description ?: "")
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Set bounds for Indonesia
        val indonesiaBounds = LatLngBounds(
            LatLng(-11.0, 95.0),   // Southwest bound
            LatLng(6.0, 141.0)     // Northeast bound
        )

        // Move camera to the bounds of Indonesia
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(indonesiaBounds, 0))
    }
}
