package com.example.mypackagedelivermanager.Entities

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ParcelDao {
   @Query("SELECT * FROM packages")
   suspend fun getAll(): List <Parcel>
}