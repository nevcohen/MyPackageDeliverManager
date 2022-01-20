package com.example.mypackagedelivermanager.Entities

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object Repository {
    private val db = Firebase.database
    private val ref = db.getReference("packages")
    private val dao : ParcelDao? = null

    fun getAllPackages(data: MutableLiveData<List<ParcelTable>>){
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val pack  : List<ParcelTable> = snapshot.children.map {
                    parcel -> parcel.getValue(ParcelTable::class.java)!!
                }
                data.postValue(pack)
            }

            override fun onCancelled(error: DatabaseError) {
             //   Toast.makeText(con)
            }

        })
    }
}