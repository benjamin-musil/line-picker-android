package com.example.apt_line_picker_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddRestaurant : MenuCommon() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_restaurant)
  }
}