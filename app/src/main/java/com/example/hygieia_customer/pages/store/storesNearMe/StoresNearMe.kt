package com.example.hygieia_customer.pages.store.storesNearMe

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.ActivityStoresNearMeBinding
import com.example.hygieia_customer.repository.StoreRepo
import com.example.hygieia_customer.utils.Commons
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class StoresNearMe : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnInfoWindowClickListener,
    GoogleMap.InfoWindowAdapter {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoresNearMeBinding
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
        private const val NEARBY_DISTANCE_THRESHOLD = 5000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoresNearMeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
        mMap.setInfoWindowAdapter(this)

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong, "You are here!")
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))

                val storeRepo = StoreRepo()
                storeRepo.getStores { success, stores, error ->
                    if (success && stores != null) {
                        for (store in stores) {
                            val coordinates = store.coordinates
                            if (coordinates != null) {
                                val latitude = coordinates["latitude"]
                                val longitude = coordinates["longitude"]
                                if (latitude != null && longitude != null) {
                                    val storeLocation = Location("").apply {
                                        this.latitude = latitude
                                        this.longitude = longitude
                                    }
                                    val distance = location.distanceTo(storeLocation)
                                    if (distance <= NEARBY_DISTANCE_THRESHOLD) {
                                        val latLng = LatLng(latitude, longitude)
                                        placeMarkerOnMap(latLng, store.name)
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                                    }
                                } else {
                                    Commons().showToast("Invalid coordinates", this)
                                }
                            } else {
                                Commons().showToast("Coordinates not found for store: ${store.name}", this)
                            }
                        }
                    } else {
                        Commons().showToast("An error occurred: $error", this)
                    }
                }
            }
        }
    }

    private fun extractLatLngFromMapUrl(mapUrl: String): Pair<Double, Double>? {
        // Regular expression pattern to match latitude and longitude
        val pattern = """@(-?\d+\.\d+),(-?\d+\.\d+)""".toRegex()
        val matchResult = pattern.find(mapUrl)
        return matchResult?.destructured?.let { (latitude, longitude) ->
            Pair(latitude.toDouble(), longitude.toDouble())
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng, storeName: String) {
        val markerOptions = MarkerOptions().position(currentLatLong).title(storeName)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
        mMap.addMarker(markerOptions)
    }

    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, "Clicked marker: ${marker.title}", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerClick(marker: Marker) = false
    override fun getInfoContents(marker: Marker): View? {
        // Inflate a custom view for the info window
        val infoWindowView = layoutInflater.inflate(R.layout.custom_store_info, null)

        // Customize the info window view as needed
        val titleTextView = infoWindowView.findViewById<TextView>(R.id.storeName)
        titleTextView.text = marker.title

        // Make the custom info window view clickable
        infoWindowView.setOnClickListener {
            // Handle the click event here
            Toast.makeText(this, "Clicked marker: ${marker.title}", Toast.LENGTH_SHORT).show()
        }

        return infoWindowView
    }

    override fun getInfoWindow(p0: Marker): View? {
        return null
    }
}