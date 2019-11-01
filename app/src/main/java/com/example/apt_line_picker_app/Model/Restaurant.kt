package com.example.apt_line_picker_app.Model

import org.json.JSONObject

class Restaurant(val id:String) {
    var address: String = ""
    var name: String = ""
    var wait_times: List<List<String>> = emptyList<List<String>>()
    var images: List<String> = emptyList<String>()
    var category: String = ""
    var geolocation: String? = ""
    var reported_by: String? =""
    var distance: String? = ""
}


class SearchedRestaurant(val id:String) {
    var address: String = ""
    var name: String = ""
    var wait_times: String? = ""
    var images: List<String> = emptyList<String>()
    var category: String = ""
    var geolocation: String? = ""
    var reported_by: String? =""
    var distance: String? = ""
}

class SearchedRestaurantList(val id:String) {
    var restaurants: List<SearchedRestaurant> = emptyList()
}