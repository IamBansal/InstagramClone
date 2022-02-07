package com.example.instagramclone.model

class User(
    Bio : String ?= null,
    Email : String? = null,
    Name : String? = null,
    Username : String? = null,
    id : String? = null,
    imageUrl : String? = null
) {
     var imageUrl = imageUrl
        get() = field
        set(value) {
            field = value
        }
     var id = id
        get() = field
        set(value) {
            field = value
        }
     var Username = Username
        get() = field
        set(value) {
            field = value
        }
    var Name = Name
        get() = field
        set(value) {
            field = value
        }
     var Email = Email
        get() = field
        set(value) {
            field = value
        }
     var Bio = Bio
        get() = field
        set(value) {
            field = value
        }
}