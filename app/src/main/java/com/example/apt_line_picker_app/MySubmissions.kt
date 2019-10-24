package com.example.apt_line_picker_app

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_submissions.*
import java.net.CookieHandler
import com.android.volley.AuthFailureError
import android.R.string.no
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log







class MySubmissions : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_submissions)
        //setSupportActionBar(findViewById(R.id.my_toolbar))
        getData()
    }

    fun getData() {

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken
        Log.e("Tag", token)
        Log.e("Tag",  FirebaseAuth.getInstance().toString())
        val currentuser = FirebaseAuth.getInstance().currentUser!!.displayName
        val gooduid = currentuser!!.replace(" ", "_")
        val url = "http://10.0.2.2:5000/mobile/"+gooduid+"/mysubmissions"
        Log.e("Tag", url)
        val jsonObjReq = object : JsonObjectRequest(Method.GET,
            url, null,
            Response.Listener { response ->
                textView3.text = "Response: %s".format(response.toString())
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
        Log.e("Tag", jsonObjReq.toString())
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
        val imageLoader: ImageLoader by lazy {
            ImageLoader(requestQueue,
                object : ImageLoader.ImageCache {
                    private val cache = LruCache<String, Bitmap>(20)
                    override fun getBitmap(url: String): Bitmap {
                        return cache.get(url)
                    }
                    override fun putBitmap(url: String, bitmap: Bitmap) {
                        cache.put(url, bitmap)
                    }
                })
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
