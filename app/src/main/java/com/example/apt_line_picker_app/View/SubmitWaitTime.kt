package com.example.apt_line_picker_app.View

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.example.apt_line_picker_app.R
import com.example.apt_line_picker_app.UserSettings
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_submit_wait_time.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class AppGlideModule : AppGlideModule()

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
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun takePicture(view: View) {
        checkCameraPermission()
        dispatchTakePictureIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Glide.with(this).load(currentPhotoPath).into(cameraImage)
//            Picasso.with(this).setIndicatorsEnabled(true)
//            var test = Picasso.with(this).isLoggingEnabled
//            Picasso.with(this)
//                .load(currentPhotoPath)
//                .resize(130, 50)
//                .centerCrop()
//                .into(findViewById<ImageView>(R.id.cameraImage)
//            )
//            Picasso.with(this).load("https://i2.wp.com/www.scottcoulthard.com/wp-content/uploads/2016/10/IMG_1816.jpg?resize=251%2C250").resize(130, 50)
//                .centerCrop().into(findViewById<ImageView>(R.id.cameraImage))
            //"/Android/data/com.example.apt_line_picker_app/files/Pictures/JPEG_20191027_151922_6109619014508195945.jpg"
            //val imageBitmap = data?.extras?.get("data") as Bitmap
            //findViewById<ImageView>(R.id.cameraImage).setImageBitmap(imageBitmap)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    // ...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.apt_line_picker_app.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
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

    // Permission check functions
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
