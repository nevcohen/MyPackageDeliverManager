package com.example.mypackagedelivermanager.Entities

class Parcel {
    var weight: String? = null
    var type: String? = null
    var addressee: String? = null
    var phone: String? = null
    var fragile: String? = null
    var longitude: String? = null
    var latitude: String? = null
    var sender: String? = null
    var pktId: String? = null

    constructor() {}

    constructor(
        weight: String?,
        type: String?,
        addressee: String?,
        phone: String?,
        fragile: String?,
        longitude: String?,
        latitude: String?,
        sender: String?,
        pktId: String?
    ) {
        this.weight = weight
        this.type = type
        this.addressee = addressee
        this.phone = phone
        this.fragile = fragile
        this.longitude = longitude
        this.latitude = latitude
        this.sender = sender
        this.pktId = pktId
    }

    fun showParcel(): String? {
        return """
            id : $pktId
            type : $type
            weight : $weight
            sender : $sender
            addressee : $addressee
            fragile : $fragile
            longitude : $longitude
            latitude : $latitude
            phone : $phone
        """
    }
}