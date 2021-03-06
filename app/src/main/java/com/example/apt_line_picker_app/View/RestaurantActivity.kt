package com.example.apt_line_picker_app.View

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.Model.Restaurant
import com.example.apt_line_picker_app.Utils.Util
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_firebase.*
import kotlinx.android.synthetic.main.activity_restaurant.*
import com.google.gson.reflect.TypeToken
import kotlin.reflect.typeOf
import com.google.gson.*
import com.google.gson.stream.JsonReader
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.synnapps.carouselview.CarouselView
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.example.apt_line_picker_app.*
import com.squareup.picasso.Picasso


class RestaurantActivity : MenuCommon() {

    var restaurantId = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkToken()
        setContentView(com.example.apt_line_picker_app.R.layout.activity_restaurant)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        token = account!!.idToken!!

        val extras = intent.extras

        restaurantId = extras?.getString("restaurantId") ?: "5db48d1a4d183fdcb32f8230"
        getRestaurant(restaurantId, token!!, this)
    }

    fun getRestaurant(id: String, idToken: String, context: Context) {
        var restaurant = Restaurant(id)
        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/restaurant/${restaurant.id}"
        val token = idToken

        val jsonObjReq = object : JsonObjectRequest(
            Method.GET,
            url, null,
            Response.Listener { response ->
                restaurantAddress.text = response["address"].toString()
                restaurantName.text = response["name"].toString()

                val restaurant = jsonToRestaurant(response)
                updateRestaurantTable(restaurant)
                fillScrollView(restaurant, context)
            },
            Response.ErrorListener { error ->
                error.toString()
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
        jsonObjReq.retryPolicy = DefaultRetryPolicy(15000,1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        // Access the RequestQueue through your singleton class.
        Util.MySingleton.getInstance(context).addToRequestQueue(jsonObjReq)
    }


    fun jsonToRestaurant(response: JSONObject): Restaurant {
        val gson = GsonBuilder().create()
        val listType2 = object : TypeToken<Restaurant>() {
        }.type

        return gson.fromJson<Restaurant>(response.toString(), listType2)
    }


    fun updateRestaurantTable(restaurant: Restaurant) {
        val tl = findViewById(com.example.apt_line_picker_app.R.id.waitTimes) as TableLayout
        if (restaurant.wait_times != null) {
            for (submission in restaurant.wait_times) {
                val tr1 = TableRow(this)

                val timeTextView = TextView(this)
                timeTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                timeTextView.text = submission[0]

                val submissionTextView = TextView(this)
                submissionTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                submissionTextView.text = submission[1]

                val reportedByTextView = TextView(this)
                reportedByTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                reportedByTextView.text = submission[2]

                tr1.addView(timeTextView)
                tr1.addView(submissionTextView)
                tr1.addView(reportedByTextView)

                tl.addView(tr1)
            }
        } else {
            val tr1 = TableRow(this)

            val timeTextView = TextView(this)
            timeTextView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            timeTextView.text = "No submissions yet"
            tr1.addView(timeTextView)
            tl.addView(tr1)
        }

    }

    fun fillScrollView(restaurant: Restaurant, context: Context) {
        var pictureView: LinearLayout = findViewById(com.example.apt_line_picker_app.R.id.imageHost)
        var picasso = Picasso.with(this)
        for (image in restaurant.images) {
            var newImage = ImageView(context)
            picasso
                .load(image).fit().placeholder(com.example.apt_line_picker_app.R.drawable.picasso)
                .into(newImage)
            pictureView.addView(newImage)
        }
    }

    fun submitWaitTime(view: View) {
        val submitIntent = Intent(this, SubmitWaitTime::class.java)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        token = account!!.idToken!!
        val extras = Bundle()
        extras.putString("restaurantId", restaurantId)
        extras.putString("token", token)
        submitIntent.putExtras(extras)

        startActivity(submitIntent)
    }

    fun checkToken() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/verify-token"

        val jsonObjReq = object : JsonObjectRequest(
            Method.GET,
            url, null,
            Response.Listener { response ->
            },
            Response.ErrorListener { error ->
                startActivity(Intent(this, FirebaseActivity::class.java))
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
        UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }


}
