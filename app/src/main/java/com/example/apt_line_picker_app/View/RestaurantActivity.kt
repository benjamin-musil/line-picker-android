package com.example.apt_line_picker_app.View

import android.content.Context
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
import android.widget.TableLayout
import android.R.attr.data
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.widget.TextView
import android.widget.TableRow
import org.json.JSONArray
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import com.google.gson.reflect.TypeToken
import kotlin.reflect.typeOf
import com.google.gson.*
import com.google.gson.stream.JsonReader
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap




class RestaurantActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.apt_line_picker_app.R.layout.activity_restaurant)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken
        getRestaurant("5d97e5b9448ed112c9660b00", token!!, this)
    }


    fun getRestaurant(id: String, idToken:String, context: Context){
        var restaurant = Restaurant(id)
        val url = "http://10.0.2.2:5000/mobile/restaurant/${restaurant.id}"
        val token = idToken

        val jsonObjReq = object : JsonObjectRequest(
            Method.GET,
            url, null,
            Response.Listener { response ->
                val gson = GsonBuilder().create()
                restaurantAddress.text = response["address"].toString()
                restaurantName.text = response["name"].toString()

                val listType2 = object : TypeToken<Restaurant>() {
                }.type

                val restaurant = gson.fromJson<Restaurant>(response.toString(), listType2)

                /** update table **/
                val tl = findViewById(R.id.waitTimes) as TableLayout
                for(i in restaurant.wait_times){
                    val tr1 = TableRow(this)
                    for(j in i!!){
                        val textview = TextView(this)
                        textview.setText(j)
                        tr1.addView(textview)
                    }
                    tl.addView(tr1)
                }
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


    fun getStringArray(jsonArray: JSONArray?): Array<String?>? {
        var stringArray: Array<String?>? = null
        if (jsonArray != null) {
            val length = jsonArray.length()
            stringArray = arrayOfNulls(length)
            for (i in 0 until length) {
                stringArray[i] = jsonArray.optString(i)
            }
        }
        return stringArray
    }


}
