package com.example.apt_line_picker_app.Model

class User {
    var name:String = ""
    var email:String=""
    var favorite_food:String=""
    var user_id:String=""
    var role:String=""
}

class UserSettings {
    var user:String =""
    var image_submissions:List<List<String>> = emptyList()
    var wait_submissions:List<List<String>> = emptyList()
}