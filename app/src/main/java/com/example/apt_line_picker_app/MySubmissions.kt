package com.example.apt_line_picker_app

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_submissions.*
import java.net.CookieHandler
import android.R.string.no
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.android.volley.*
import com.example.apt_line_picker_app.Model.Restaurant
import com.example.apt_line_picker_app.Model.UserSettings
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_firebase.*
import org.json.JSONObject


class MySubmissions : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_submissions)
        getData()
    }

    fun getData() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken
        val currentuser = FirebaseAuth.getInstance().currentUser!!.displayName
        val gooduid = currentuser!!.replace(" ", "_")
        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/"+gooduid+"/mysubmissions"
        val jsonObjReq = object : JsonObjectRequest(Method.GET,
            url, null,
            Response.Listener { response ->

                val userSetting = jsonToUserSettings(response)
                fillImageTable(userSetting.image_submissions)
                fillWaitTable(userSetting.wait_submissions)
            },
            Response.ErrorListener { error ->
                textView3.text = error.toString()
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
            DefaultRetryPolicy(25000,1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
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

    fun jsonToUserSettings(response: JSONObject): UserSettings {
        val gson = GsonBuilder().create()
        val listType2 = object : TypeToken<UserSettings>() {
        }.type

        return gson.fromJson<UserSettings>(response.toString(), listType2)
    }

    fun fillImageTable(imageList: List<List<String>>){
        val tl = findViewById(com.example.apt_line_picker_app.R.id.ImageUrlTable) as TableLayout
        if (imageList.size > 0) {
            for( image in imageList) {
                val tr1 = TableRow(this)

                val imageView = TextView(this)
                imageView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                imageView.text = image[0]
                val restaurantView = TextView(this)
                restaurantView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                restaurantView.text = image[2]
                tr1.addView(imageView)
                tr1.addView(restaurantView)
                tl.addView(tr1)
            }
        } else{
            val tr1 = TableRow(this)

            val imageView = TextView(this)
            imageView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            imageView.text = "Nono submitted"
            val restaurantView = TextView(this)
            restaurantView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            restaurantView.text = "Submit one!"
            tr1.addView(imageView)
            tr1.addView(restaurantView)
            tl.addView(tr1)
        }

    }

    fun fillWaitTable(waitList: List<List<String>>) {
        val tl = findViewById(com.example.apt_line_picker_app.R.id.WaitTime) as TableLayout
        for( submission in waitList) {
            val tr1 = TableRow(this)

            val timeView = TextView(this)
            timeView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            timeView.text = submission[0]
            val dateView = TextView(this)
            dateView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            dateView.text = submission[1]

            val restaurantView = TextView(this)
            restaurantView.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            restaurantView.text = submission[2]


            tr1.addView(timeView)
            tr1.addView(dateView)
            tr1.addView(restaurantView)
            tl.addView(tr1)
        }
    }

}