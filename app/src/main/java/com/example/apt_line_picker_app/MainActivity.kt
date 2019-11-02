package com.example.apt_line_picker_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        startActivity(Intent(this, HomeAndMenu::class.java))
        checkToken()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && FirebaseAuth.getInstance().currentUser != null) {
            findViewById<TextView>(R.id.user).text = account!!.email.toString()
            startActivity(Intent(this, HomeAndMenu::class.java))
        } else {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }
        super.onCreate(savedInstanceState)
    }

    fun signOut(view: View) {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                finish();
                startActivity(getIntent());
            }
    }

    fun checkToken() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken

        val url = "https://"+getString(R.string.base_url)+"/mobile/verify-token"

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
