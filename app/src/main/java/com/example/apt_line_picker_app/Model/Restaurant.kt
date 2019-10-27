package com.example.apt_line_picker_app.Model

class Restaurant(val id:String) {
    var address:String = ""
    var name:String = ""
    var wait_times:List<List<String>> = emptyList<List<String>>()
    var images: List<String> = emptyList<String>()
    var geolocation:String = ""
}