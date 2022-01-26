package com.example.mypackagedelivermanager

import com.example.mypackagedelivermanager.Entities.Parcel
import com.google.firebase.database.*


class Firebase_Manager_Parcel {
    var ParcelsRef: DatabaseReference? = null
    var ParcelList: MutableList<Parcel>? = null
    var parcelRefChildEventListener: ChildEventListener? = null

    interface Action<T> {
        fun onSuccess(obj: T)
        fun onFailure(exception: Exception?)
    }

    interface NotifyDataChange<T> {
        fun OnDataChanged(obj: T)
        fun onFailure(exception: Exception?)
    }

    init {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        ParcelsRef = database.getReference("packages")
        ParcelList = mutableListOf()
    }

    fun addParcelToFirebase(parcel: Parcel, action: Action<String>) {
        val key = ParcelsRef!!.push().key.toString()
        parcel.pktId = key
        ParcelsRef!!.child(key).setValue(parcel)
            .addOnSuccessListener {
                action.onSuccess(parcel.pktId!!)
            }.addOnFailureListener { e ->
                action.onFailure(e)
            }
    }


    fun notifyToParcelList(notifyDataChange: NotifyDataChange<MutableList<Parcel>>) {
        if (notifyDataChange != null) {
            if (parcelRefChildEventListener != null) {
                notifyDataChange.onFailure(Exception("first unNotify student list"))
                return
            }
            ParcelList!!.clear()
            parcelRefChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val parcel: Parcel? = snapshot.getValue(Parcel::class.java)
                    ParcelList!!.add(parcel!!)
                    notifyDataChange.OnDataChanged(ParcelList!!)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val parcel: Parcel? = snapshot.getValue(Parcel::class.java)
                    val key: String? = parcel!!.pktId

                    for (i in 0..ParcelList!!.size)
                        if (ParcelList!![i].pktId.equals(key)) {
                            ParcelList!![i] = parcel
                            break
                        }
                    notifyDataChange.OnDataChanged(ParcelList!!)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val parcel: Parcel? = snapshot.getValue(Parcel::class.java)
                    val key: String? = parcel!!.pktId

                    for (i in 0..ParcelList!!.size)
                        if (ParcelList!![i].pktId.equals(key)) {
                            ParcelList!!.removeAt(i)
                            break
                        }
                    notifyDataChange.OnDataChanged(ParcelList!!)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    notifyDataChange.onFailure(error.toException())
                }
            }
            ParcelsRef!!.addChildEventListener(parcelRefChildEventListener!!)
        }
    }

    fun stopNotifyToStudentList() {
        if (parcelRefChildEventListener != null) {
            ParcelsRef!!.removeEventListener(parcelRefChildEventListener!!)
            parcelRefChildEventListener = null
        }
    }
}