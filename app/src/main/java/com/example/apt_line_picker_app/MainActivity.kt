package com.example.apt_line_picker_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        var auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            findViewById<TextView>(R.id.user).text = auth.currentUser!!.email.toString()
        } else {
            startActivity(Intent(this, FirebaseActivity::class.java))
        }
        super.onCreate(savedInstanceState)
    }

    fun signOut(view: View) {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    companion object {

        private const val RC_SIGN_IN = 123
    }
}
