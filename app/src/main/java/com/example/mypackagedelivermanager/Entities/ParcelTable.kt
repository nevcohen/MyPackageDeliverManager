package com.example.mypackagedelivermanager.Entities

import androidx.databinding.BaseObservable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "packages")
data class ParcelTable(@PrimaryKey var pkgid:String? = null) : BaseObservable(){
    var type : String? = null
    var weight : String? = null
    var addressee : String? = null
    var phone : String? = null
    var fragile : String? = null
    var longitude : String? = null
    var latitude : String? = null
    var sender: String? = null
    var pktId: String? = null
}





