package com.example.mypackagedelivermanager

import com.example.mypackagedelivermanager.Entities.User
import com.google.firebase.database.*


class Firebase_Manager_User {
    var UsersRef: DatabaseReference? = null
    var UserList: MutableList<User>? = null
    var userRefChildEventListener: ChildEventListener? = null

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
        UsersRef = database.getReference("users")
        UserList = mutableListOf()
    }

    fun addParcelToFirebase(user: User, action: Action<String>) {
        val key = UsersRef!!.push().key.toString()
        UsersRef!!.child(key).setValue(user)
            .addOnSuccessListener {
                action.onSuccess(key)
            }.addOnFailureListener { e ->
                action.onFailure(e)
            }
    }


    fun notifyToParcelList(notifyDataChange: NotifyDataChange<MutableList<User>>) {
        if (notifyDataChange != null) {
            if (userRefChildEventListener != null) {
                notifyDataChange.onFailure(Exception("first unNotify student list"))
                return
            }
            UserList!!.clear()
            userRefChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User? = snapshot.getValue(User::class.java)
                    UserList!!.add(user!!)
                    notifyDataChange.OnDataChanged(UserList!!)
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val user: User? = snapshot.getValue(User::class.java)
                    val key: Int? = user!!.user_id

                    for (i in 0..UserList!!.size)
                        if (UserList!![i].user_id == key) {
                            UserList!![i] = user
                            break
                        }
                    notifyDataChange.OnDataChanged(UserList!!)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    val user: User? = snapshot.getValue(User::class.java)
                    val key: Int? = user!!.user_id

                    for (i in 0..UserList!!.size)
                        if (UserList!![i].user_id == key) {
                            UserList!!.removeAt(i)
                            break
                        }
                    notifyDataChange.OnDataChanged(UserList!!)
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    notifyDataChange.onFailure(error.toException())
                }
            }
            UsersRef!!.addChildEventListener(userRefChildEventListener!!)
        }
    }

    fun stopNotifyToStudentList() {
        if (userRefChildEventListener != null) {
            UsersRef!!.removeEventListener(userRefChildEventListener!!)
            userRefChildEventListener = null
        }
    }
}