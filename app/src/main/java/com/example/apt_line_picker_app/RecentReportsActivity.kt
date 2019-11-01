package com.example.apt_line_picker_app

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.Model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_user_settings.*


class RecentReportsActivity : MenuCommon(), OnMapReadyCallback, OnMarkerClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private lateinit var lastLocation: Location
    }

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_reports)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getData(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location


                // UT Engineering Building for debug
                val currentLatLng = LatLng(30.2875301, -97.7360316)

                // Use actual location of device
                //val currentLatLng = LatLng(location.latitude, location.longitude)

                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                // Show nearby recent reports
                // Place this in a loop that goes through an array of report data and extract restaurant id/title, location,
                // and wait time (and time of that report submission)
                //placeNearbyMarkers(lastlocation)

                val testLatLng = LatLng(30.336235, -97.617303) // Applied Materials Austin
                placeMarkerOnMap(testLatLng, "Applied Material's", "Hope I'm not here long!")
            }
        }
    }

    private fun placeNearbyMarkers(location: LatLng) {

    }

    private fun placeMarkerOnMap(location: LatLng,restaurantTitle: String = "",
                                 waitTime: String = "") {
        // 1
        val markerOptions =
            MarkerOptions().position(location).title(restaurantTitle).snippet(waitTime)
        // 2
        map.addMarker(markerOptions)
    }

    fun getData(context: Context) {
        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/recent-reports" // replace with url to get recent searches

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        val jsonObjReq = object : JsonObjectRequest(
            Method.GET,
            url, null,
            Response.Listener { response ->
                run {
                    val user = jsonToUser(response["user"].toString())
                    fillUserSettings(user, context)
                }
            },
            Response.ErrorListener { error ->
                textView2.visibility = View.VISIBLE
                textView2.text = error.toString()
            }) {
            /** Passing some request headers*  */
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Content-Type", "application/json")
                headers.put("token", token!!)
                return headers
            }
        }

        // Access the RequestQueue through your singleton class.
        UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }

    fun jsonToUser(response: String): User {
        val user = Gson().fromJson(response.toString(), User::class.java)
        return user;
    }

    fun fillUserSettings(user: User, context: Context) {
        Header.text = user.user_id.toString()
        EmailId.text = user.email.toString()
        for (i in 1 until RestaurantCategory.childCount) {
            val id = RestaurantCategory.getChildAt(i).getId()
            val category = findViewById<RadioButton>(id)
            if (category.text == user.favorite_food) {
                category.isChecked = true
            }

        }
    }
}

