package com.example.apt6android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class MySubmissions : MenuCommon() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //findViewById<ListView>(R.id.WaitTimeList)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_submissions)


    }
}
