package com.example.apt_line_picker_app.View

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.Model.Restaurant
import com.example.apt_line_picker_app.R
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
import com.squareup.picasso.Picasso



class RestaurantActivity : AppCompatActivity() {

    var restaurantId = ""
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.apt_line_picker_app.R.layout.activity_restaurant)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        token = account!!.idToken!!
        restaurantId = "5d97e5b9448ed112c9660b00"
        getRestaurant(restaurantId, token!!, this)
    }

    fun getRestaurant(id: String, idToken:String, context: Context){
        var restaurant = Restaurant(id)
        val url = "http://10.0.2.2:5000/mobile/restaurant/${restaurant.id}"
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

        // Access the RequestQueue through your singleton class.
        Util.MySingleton.getInstance(context).addToRequestQueue(jsonObjReq)
    }


    fun jsonToRestaurant(response: JSONObject):Restaurant {
        val gson = GsonBuilder().create()
        val listType2 = object : TypeToken<Restaurant>() {
        }.type

        return gson.fromJson<Restaurant>(response.toString(), listType2)
    }


    fun updateRestaurantTable(restaurant: Restaurant){
        val tl = findViewById(com.example.apt_line_picker_app.R.id.waitTimes) as TableLayout
        for(submission in restaurant.wait_times){
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
    }

    fun fillScrollView(restaurant: Restaurant, context: Context) {
        var pictureView:LinearLayout = findViewById(com.example.apt_line_picker_app.R.id.imageHost)
        var picasso = Picasso.with(this)
        for(image in restaurant.images) {
            var newImage = ImageView(context)
            picasso
                .load(image).fit().placeholder(com.example.apt_line_picker_app.R.drawable.picasso)
                .into(newImage)
            pictureView.addView(newImage)
        }
    }

    fun submitWaitTime(view: View){
        val submitIntent = Intent(this, SubmitWaitTime::class.java)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        token = account!!.idToken!!
        val extras = Bundle()
        extras.putString("restaurantId", "5d97e5b9448ed112c9660b00")
        extras.putString("token", token)
        submitIntent.putExtras(extras)

        startActivity(submitIntent)
    }

}
