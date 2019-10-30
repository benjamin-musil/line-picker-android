package com.example.apt_line_picker_app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.View
import android.widget.*
import com.android.volley.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_user_settings.*
import java.net.CookieHandler
import com.android.volley.toolbox.*
import com.example.apt_line_picker_app.Model.Restaurant
import com.example.apt_line_picker_app.Model.User
import com.example.apt_line_picker_app.View.RestaurantActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_restaurant.*
import org.json.JSONArray
import org.json.JSONObject




class UserSettings : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkToken()
        setContentView(R.layout.activity_user_settings)
        getCategoryData(this)
        getData(this)
    }

    fun getData(context: Context) {
        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/user-settings"

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        val jsonObjReq = object : JsonObjectRequest(Method.GET,
            url, null,
            Response.Listener { response ->
                run {
                    textView2.visibility = View.INVISIBLE
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }

    fun getCategoryData(context: Context) {
        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/get-all-categories"

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        val jsonObjReq = object : JsonArrayRequest(Method.GET,
            url, null,
            Response.Listener { response ->
                addCategories(response, this)
            },
            Response.ErrorListener { error ->
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }

    fun addCategories(categories: JSONArray, context: Context) {
        for (i in 0 until categories.length()) {
            val categoryradiobutton = RadioButton(this)
            categoryradiobutton.text = categories[i].toString()
            RestaurantCategory.addView(categoryradiobutton)
        }
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

    fun saveUserSettings(view: View) {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken
        val idSelected = RestaurantCategory.checkedRadioButtonId
        if (idSelected != -1) {
            val radio: RadioButton = findViewById(idSelected)
            val url = "http://"+getString(R.string.local_ip)+":5000/mobile/update-user"
            val category: String = radio.text.toString()
            var params = HashMap<String, String>()
            params.put("category", category)

            val jsonObjReq = object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(params.toMap()),
                Response.Listener { response ->
                    Toast.makeText(
                        applicationContext,
                        "Category Changed To " + radio.text,
                        Toast.LENGTH_SHORT
                    ).show()
                },
                Response.ErrorListener { error ->
                    Log.d(null, error.toString())
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
                DefaultRetryPolicy(
                    15000, 1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )

            // Access the RequestQueue through your singleton class.
            UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
        }

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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }

}
