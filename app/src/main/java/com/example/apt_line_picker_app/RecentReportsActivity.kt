package com.example.apt_line_picker_app

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.example.apt_line_picker_app.Model.SearchedRestaurant
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
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.android.volley.Response;
import com.example.apt_line_picker_app.Utils.MyJsonArrayRequest
import org.json.JSONObject;

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

                // Use actual location of device
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))

                // Show nearby recent reports
                getData(this, location.latitude, location.longitude)
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng, restaurantTitle: String = "",
                                 waitTime: String? = "") {
        // Compile marker options
        val markerOptions =
            MarkerOptions().position(location).title(restaurantTitle).snippet(waitTime + " minute wait")
        // Create marker
        map.addMarker(markerOptions)
    }

    private fun getData(context: Context, latitude: Double, longitude: Double) {
        val url = "http://"+getString(R.string.base_url)+"/mobile/recent-reports"

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        var params = HashMap<String, String>()
        params.put("lat1", latitude.toString())
        params.put("long1", longitude.toString())

        val jsonArrayReq = object : MyJsonArrayRequest(
            Method.POST,
            url, JSONObject(params.toMap()),
            Response.Listener { response ->
                // On good response, get results of nearby restaurant submissions
                // Only loops once, JsonArrays do not have iterators built in
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)

                    val result = jsonToRestaurant(item)
                    createReportMarkers(result)
                }
            },
            Response.ErrorListener { error ->
                // Error message here
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
                jsonArrayReq.setRetryPolicy(
            DefaultRetryPolicy(150000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )

        // Access the RequestQueue through your singleton class.
       MySingleton.getInstance(this).addToRequestQueue(jsonArrayReq)
    }

    fun jsonToRestaurant(response: JSONObject): SearchedRestaurant {
        val gson = GsonBuilder().create()
        val listType2 = object : TypeToken<SearchedRestaurant>() {
        }.type

        return gson.fromJson<SearchedRestaurant>(response.toString(), listType2)
    }

    fun createReportMarkers(result: SearchedRestaurant) {

        // Get latitude and longitude of restaurant
        // Regex
        val pattern = Regex("[.\\d-]+")
        val matchResults = pattern.findAll(result.geolocation.toString())
        var matches : MutableList<String> = mutableListOf<String>()
        matchResults.forEach { match ->
            matches.add(match.value)
        }

        // assign values
        val restaurantLat = matches[0].toDouble()
        val restaurantLong = matches[1].toDouble()
        val restaurantLatLng = LatLng(restaurantLat, restaurantLong)

        // Create marker for restaurant on map
        placeMarkerOnMap(restaurantLatLng, result.name, result.wait_times)

    }

    class MySingleton constructor(context: Context) {
        companion object {
            @Volatile
            private var INSTANCE: MySingleton? = null
            fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: MySingleton(context).also {
                        INSTANCE = it
                    }
                }
        }
        val requestQueue: RequestQueue by lazy {
            // applicationContext is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            Volley.newRequestQueue(context.applicationContext)
        }
        fun <T> addToRequestQueue(req: Request<T>) {
            requestQueue.add(req)
        }
    }

}
