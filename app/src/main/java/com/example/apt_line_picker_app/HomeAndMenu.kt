package com.example.apt_line_picker_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.apt_line_picker_app.View.AddRestaurant
import com.example.apt_line_picker_app.View.SearchActivity

class HomeAndMenu : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_and_menu)
    }
    fun SearchRestaurant(view: View) {
        val SearchIntent = Intent(this, SearchActivity::class.java)
        startActivity(SearchIntent)
    }

    fun ViewRecentReports(view: View) {
        val ReportsIntent = Intent(this, RecentReportsActivity::class.java)
        startActivity(ReportsIntent)
    }


    fun AddRestaurant(view:View) {

        val AddRestaurantIntent=Intent(this, AddRestaurant::class.java)
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
