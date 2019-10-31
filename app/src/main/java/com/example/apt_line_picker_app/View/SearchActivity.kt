package com.example.apt_line_picker_app.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.apt_line_picker_app.MenuCommon
import androidx.core.view.MenuItemCompat.getActionView
import android.R
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*
import androidx.databinding.adapters.SearchViewBindingAdapter
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.Model.Restaurant
import com.example.apt_line_picker_app.Model.SearchedRestaurant
import com.example.apt_line_picker_app.Model.SearchedRestaurantList
import com.example.apt_line_picker_app.MySubmissions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_my_submissions.*
import org.json.JSONObject


class SearchActivity : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.apt_line_picker_app.R.layout.activity_search)

        val ourSearchItem = findViewById<SearchView>(com.example.apt_line_picker_app.R.id.search)

        ourSearchItem.queryHint = "Chinese"
        ourSearchItem.isSubmitButtonEnabled = true
        ourSearchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                getData(query)

                return false
            }

        })

    }

    fun getData(query:String) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        var params = HashMap<String, String>()
        params.put("restaurant_tag", query)


        val url = "http://"+getString(com.example.apt_line_picker_app.R.string.local_ip)+":5000/mobile/ListAllRestaurant/Search"
        val jsonObjReq = object : JsonObjectRequest(
            Method.POST,
            url, JSONObject(params.toMap()),
            Response.Listener { response ->
                val results = jsonToRestaurant(response)
                fillResultsTable(results.restaurants)
            },
            Response.ErrorListener { error ->
//                textView3.text = error.toString()
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
        jsonObjReq.setRetryPolicy(
            DefaultRetryPolicy(25000,0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )
        // Access the RequestQueue through your singleton class.
        MySubmissions.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }

    fun fillResultsTable(results: List<SearchedRestaurant>) {
        val table = findViewById<TableLayout>(com.example.apt_line_picker_app.R.id.results)
        for (restaurant in results) {
            val row = TableRow(this)
            val name = TextView(this)
            name.text = restaurant.name
            val address = TextView(this)
            address.text = restaurant.address
            val waitTime = TextView(this)
            waitTime.text = restaurant.wait_times
            val by = TextView(this)
            by.text = restaurant.resported_by
            val image = ImageView(this)
            row.addView(name)
            row.addView(address)
            row.addView(waitTime)
            row.addView(by)
            table.addView(row)


        }
    }



    fun jsonToRestaurant(response: JSONObject): SearchedRestaurantList {
        val gson = GsonBuilder().create()
        val listType2 = object : TypeToken<SearchedRestaurantList>() {
        }.type

        return gson.fromJson<SearchedRestaurantList>(response.toString(), listType2)
    }
}

