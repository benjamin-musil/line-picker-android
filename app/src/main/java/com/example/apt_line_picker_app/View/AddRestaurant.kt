package com.example.apt_line_picker_app.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.apt_line_picker_app.R
import kotlinx.android.synthetic.main.activity_firebase.view.*
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.apt_line_picker_app.UserSettings
import com.google.android.gms.auth.api.signin.GoogleSignIn
import org.json.JSONObject


class AddRestaurant : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.apt_line_picker_app.R.layout.activity_add_restaurant)
    }

    fun submitRestaurant(view: View){

        val account = GoogleSignIn.getLastSignedInAccount(this)
        val token = account!!.idToken


        val url = "http://"+getString(R.string.local_ip)+":5000/mobile/submit-restaurant"
        val address:String = findViewById<EditText>(com.example.apt_line_picker_app.R.id.RestaurantAddress).text.toString()
        val name:String = findViewById<EditText>(com.example.apt_line_picker_app.R.id.RestaurantName).text.toString()
        val category:String = findViewById<RadioButton>(findViewById<RadioGroup>(com.example.apt_line_picker_app.R.id.RestaurantCategory).checkedRadioButtonId).text.toString()

        var params = HashMap<String, String>()
        params.put("Address", address)
        params.put("Name", name)
        params.put("category", category)

        val jsonObjReq = object : JsonObjectRequest(
            Method.POST,
            url, JSONObject(params.toMap()),
            Response.Listener { response ->
                val restaurantIntent = Intent(this, RestaurantActivity::class.java)
                val extras = Bundle()
                extras.putString("restaurantId", response["id"].toString())
                extras.putString("token", token)
                restaurantIntent.putExtras(extras)
                startActivity(restaurantIntent)
            },
            Response.ErrorListener { error ->
                Log.d(null, error.toString())
//                startActivity(Intent(this, RestaurantActivity::class.java))
            })
        {
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
            DefaultRetryPolicy(15000,1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        )

        // Access the RequestQueue through your singleton class.
        UserSettings.MySingleton.getInstance(this).addToRequestQueue(jsonObjReq)


    }


}
