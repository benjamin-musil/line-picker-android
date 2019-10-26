package com.example.apt6android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_home_screen.*
import kotlinx.android.synthetic.main.activity_home_screen.fab
import kotlinx.android.synthetic.main.activity_home_screen.toolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_settings -> {
//                Toast.makeText(applicationContext, "click on setting", Toast.LENGTH_LONG).show()
//                true
//            }
//            R.id.action_share ->{
//                Toast.makeText(applicationContext, "click on share", Toast.LENGTH_LONG).show()
//                return true
//            }
//            R.id.action_exit ->{
//                val UserSettingsIntent= Intent(this,UserSettings::class.java)
//                startActivity(UserSettingsIntent)
//                //Toast.makeText(applicationContext, "click on exit", Toast.LENGTH_LONG).show()
//                return true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
}

