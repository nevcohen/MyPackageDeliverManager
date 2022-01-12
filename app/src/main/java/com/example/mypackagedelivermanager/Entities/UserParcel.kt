package com.example.mypackagedelivermanager.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * class that represent an user
 */
@Entity(tableName = "parcel_users_table")
data class UserParcel
    (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "address")
    var address: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "first_name")
    val first_name: String,
    @ColumnInfo(name = "last_name")
    val last_name: String,
    @ColumnInfo(name = "user_id")
    val user_id: Int,
    @ColumnInfo(name = "phone")
    val phone: String,
)