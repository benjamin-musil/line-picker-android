package com.example.apt_line_picker_app.View

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
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
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_IMAGE_CAPTURE = 1
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

    // Camera stuff here
    fun takePicture(view: View) {
        checkCameraPermission()
        dispatchTakePictureIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            findViewById<ImageView>(R.id.cameraImage).setImageBitmap(imageBitmap)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    fun clickSubmit(view: View) {
        // If user does not allow location, do not allow wait time submission
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
            jsonObjReq.retryPolicy = DefaultRetryPolicy(15000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

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

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
            return
        }
    }


}
