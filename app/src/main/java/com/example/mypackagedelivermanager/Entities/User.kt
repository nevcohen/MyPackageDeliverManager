package com.example.mypackagedelivermanager.Entities

class User {
    var email: String? = null
    var first_name: String? = null
    var key: String? = null
    var last_name: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var user_id: Int? = null
    var phone: String? = null


    constructor() {}
    constructor(
        email: String?,
        first_name: String?,
        last_name: String?,
        latitude: String?,
        longitude: String?,
        user_id: Int?,
        phone: String?
    ) {
        this.email = email
        this.first_name = first_name
        this.last_name = last_name
        this.latitude = latitude
        this.longitude = longitude
        this.user_id = user_id
        this.phone = phone
    }

}