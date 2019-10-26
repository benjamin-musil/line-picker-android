package com.example.apt_line_picker_app.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apt_line_picker_app.R
import com.example.apt_line_picker_app.UserSettings
import org.json.JSONObject

class SubmitWaitTime : AppCompatActivity() {

    companion object {
        const val restaurantId = ""
        const val token = ""
    }

    var bleh = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.apt_line_picker_app.R.layout.activity_submit_wait_time)
    }


    fun daRealRealSubmit(view: View) {

        val extras = intent.extras
        var params = HashMap<String, String>()


        params["Id"] = extras!!.getString("restaurantId").toString()

        var editText = findViewById<EditText>(com.example.apt_line_picker_app.R.id.waitTimeNumber2)
        params["wait"] = editText.text.toString()


        val url = "http://10.0.2.2:5000/mobile/submit-time"
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

        // Access the RequestQueue through your singleton class.
        UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)
    }





}
