package com.example.apt_line_picker_app.View

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.R
import com.example.apt_line_picker_app.UserSettings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject

class SubmitWaitTime : AppCompatActivity() {
    // Track if location access has been granted and create location object
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private lateinit var lastLocation: Location
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_wait_time)

        // Start a location client on activity start
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    fun clickSubmit(view: View) {
        checkLocationPermission()
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            val extras = intent.extras
            var params = HashMap<String, String>()

            params["Id"] = extras!!.getString("restaurantId").toString()

            var editText = findViewById<EditText>(R.id.waitTimeNumber2)
            params["wait"] = editText.text.toString()

            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                params["geolocation"] = LatLng(location.latitude, location.longitude).toString()
            }

            val url = "http://"+getString(R.string.local_ip)+":5000/mobile/submit-time"
            val jsonObjReq = object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(params.toMap()),
                Response.Listener { response ->
                    startActivity(Intent(this, RestaurantActivity::class.java))
                },
                Response.ErrorListener { error ->
                    startActivity(Intent(this, RestaurantActivity::class.java))
                })
            {
                /** Passing some request headers*  */
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Content-Type", "application/json")
                    headers.put("token", extras!!.getString("token").toString())
                    return headers
                }
            }
            jsonObjReq.retryPolicy = DefaultRetryPolicy(15000,1,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

            // Access the RequestQueue through your singleton class.
            UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
    }
}
