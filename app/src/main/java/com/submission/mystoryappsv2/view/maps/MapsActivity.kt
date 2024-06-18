package com.submission.mystoryappsv2.view.maps


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
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
        mMap.clear()
        stories.forEach { story ->
            val latLng = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val indonesiaBounds = LatLngBounds(
            LatLng(-11.0, 95.0),
            LatLng(6.0, 141.0)
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(indonesiaBounds, 0))
    }
}
