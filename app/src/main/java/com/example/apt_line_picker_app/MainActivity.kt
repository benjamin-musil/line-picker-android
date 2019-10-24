package com.example.apt_line_picker_app
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.firebase.ui.auth.AuthUI
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null && FirebaseAuth.getInstance().currentUser != null) {
            findViewById<TextView>(R.id.user).text = account!!.email.toString()
            startActivity(Intent(this, MySubmissions::class.java))
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
}
