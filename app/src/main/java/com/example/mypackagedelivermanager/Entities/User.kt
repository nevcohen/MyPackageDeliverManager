package com.example.mypackagedelivermanager.Entities

class User {
    var address: String? = null
    var email: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var user_id: Int? = null
    var phone: String? = null


    constructor() {}
    constructor(
        address: String?,
        email: String?,
        first_name: String?,
        last_name: String?,
        user_id: Int?,
        phone: String?
    ) {
        this.address = address
        this.email = email
        this.first_name = first_name
        this.last_name = last_name
        this.user_id = user_id
        this.phone = phone
    }

}