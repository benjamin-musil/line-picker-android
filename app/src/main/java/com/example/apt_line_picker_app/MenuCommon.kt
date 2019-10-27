package com.example.apt_line_picker_app

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


abstract class MenuCommon: AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_homeScreen -> {
                val HomeScreenIntent= Intent(this,HomeAndMenu::class.java)
                startActivity(HomeScreenIntent)
                return true
            }
            R.id.action_mySubmissions -> {
                val MySubmissionsIntent= Intent(this,MySubmissions::class.java)
                startActivity(MySubmissionsIntent)
                return true
            }
            R.id.action_addRestuarrant -> {
                val AddRestaurantIntent= Intent(this,AddRestaurant::class.java)
                startActivity(AddRestaurantIntent)
                return true
            }
            R.id.action_userSettings ->{
                val UserSettingsIntent= Intent(this,UserSettings::class.java)
                startActivity(UserSettingsIntent)
                return true

            }
            R.id.action_exitApplication ->{
                ActivityCompat.finishAffinity(this)
                 return  true;
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}