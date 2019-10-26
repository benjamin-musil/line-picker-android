package com.example.apt6android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HomeAndMenu : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_and_menu)
    }
    fun SearchRestaurant(view: View) {
        val SearchIntent = Intent(this, AddRestaurant::class.java)
        startActivity(SearchIntent)
    }
    fun AddRestaurant(view:View) {

        val AddRestaurantIntent=Intent(this,AddRestaurant::class.java)
        startActivity(AddRestaurantIntent)
    }
    fun MySubmissions(view:View)
    {
        val MySubmissionsIntent=Intent(this,MySubmissions::class.java)
        startActivity(MySubmissionsIntent)

    }
    fun Settings(view:View)
    {
        val SettingsIntent=Intent(this,UserSettings::class.java)
        startActivity(SettingsIntent)

    }
}
