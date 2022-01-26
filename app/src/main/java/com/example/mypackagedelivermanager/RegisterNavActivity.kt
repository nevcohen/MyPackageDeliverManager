package com.example.mypackagedelivermanager


import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.mypackagedelivermanager.Entities.Parcel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class RegisterNavActivity : Fragment() {

    var firebaseDatabase: FirebaseDatabase = Firebase.database
    var firebaseAuth = FirebaseAuth.getInstance()
    var p: Array<Parcel>? = null
    var myActivity: FragmentActivity? = null
    //  val email = firbaseAuth.currentUser?.email
    //  val phone = intent.getStringExtra("phone")
    //  private lateinit var model: viewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_register_nav, container, false)
        myActivity = activity
        val addSpinner = view.findViewById<Spinner>(R.id.pkt_spinner)
        val x: TextView = view.findViewById(R.id.textParcels)
        val canSendRefRef = firebaseDatabase.getReference("packages by can sends").push()
        val sendButton: Button = view.findViewById(R.id.finishAndStoreButton)
        sendButton.visibility = View.GONE
        val sendPack: Switch = view.findViewById(R.id.avilableSend)
        sendPack.visibility = View.INVISIBLE

        sendPack.text = "want to send this package?"
        //val ids = fromParcelsToId(parcels)
        //  parcelSpinnerForRegister(ids, parcels, addSpinner)

        getAllPackages(object : GetPkt {
            override fun onGet(value: Array<Parcel>?) {
                if (value != null) {
                    val idPkt: MutableList<String> = mutableListOf()
                    for (parcel in value)
                        idPkt.add(parcel.pktId.toString())
                    parcelSpinnerForRegister(idPkt, value.toList(), addSpinner, view)
                }
                p = value
            }
        })


        val packageMap = mutableMapOf<Parcel, ArrayList<String>>()
        if (p != null) {
            for (parcel in p!!) {
                packageMap[parcel] = java.util.ArrayList()
            }
        }

        sendButton.visibility = View.VISIBLE
        val test: TextView = view.findViewById(R.id.test)

        sendButton.setOnClickListener {

            if (sendPack.isChecked) {
                val position = addSpinner.selectedItemPosition

                val testText = test.text
                if (packageMap[p?.get(position)]?.let { it1 ->
                        notIbTheList(
                            it1,
                            testText.toString()
                        )
                    } == true) {
                    packageMap[p?.get(position)]?.add(testText.toString())
                    canSendRefRef.setValue(
                        p?.get(position) to packageMap[p?.get(
                            position
                        )]
                    )
                }
                // if (packageMap[parcels[position]]?.let { it1 -> notIbTheList(it1, testText.toString()) } == true) {
                //   packageMap[parcels[position]]?.add(testText.toString())
                //        x.text = packageMap[parcels[position]].toString()

                // }

                //canSendRefRef.setValue(parcels[position] to packageMap[parcels[position]])
            }
        }
        return view
    }

    private fun parcelSpinnerForRegister(
        systemPaks: List<String>,
        parcels: List<Parcel>,
        addSpinner: Spinner, view: View
    ) {
        var pos = 0
        val arrayAdapter = ArrayAdapter(myActivity!!, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val availableSwitch: Switch =
            view.findViewById<Switch>(com.example.mypackagedelivermanager.R.id.avilableSend)
        availableSwitch.visibility = View.VISIBLE
        addSpinner.prompt = "Select package by its id"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter
        addSpinner.visibility = View.VISIBLE
        val x: TextView =
            view.findViewById<TextView>(com.example.mypackagedelivermanager.R.id.textParcels)

        addSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val parcel: Parcel = parcels[position]
                val parcelString = fromParcelToString(parcel)
                addSpinner.setSelection(position)
                x.text = parcelString
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                pktOwnerName = "Unknown"
            }
        }
    }


    private fun getAllPackages(getNameOfPkt: GetPkt) {
        val pktListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val systemPkts: MutableList<Parcel> = mutableListOf()
                for (curPkt in dataSnapshot.children) {
                    val parcel = curPkt.getValue<Parcel>()
                    if (parcel != null) {
                        systemPkts.add(parcel)
                    }
                }
                getNameOfPkt.onGet(systemPkts.toTypedArray())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        firebaseDatabase!!.getReference("packages").addValueEventListener(pktListener)
    }

    private fun parcelSpinnerForFriends(systemPaks: List<String>, view: View) {
        val arrayAdapter =
            ArrayAdapter(myActivity!!, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val addSpinner = view.findViewById<Spinner>(R.id.pkt_spinner)
        addSpinner.prompt = "Select package"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter
        val x: TextView = view.findViewById<TextView>(R.id.textParcels)

        addSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                pktOwnerName = "Unknown"
            }
        }
    }
}

private fun fromParcelToString(parcel: Parcel): String? = parcel.showParcel()

private fun getParcelById(id: String, parcels: List<Parcel>): Parcel? {
    for (parcel in parcels) {
        if (id == parcel.pktId) return parcel

    }
    return null
}

private fun fromParcelsToId(parcels: List<Parcel>): MutableList<String> {
    val idParcels: MutableList<String> = arrayListOf()
    for (parcel in parcels) {
        parcel.pktId?.let { idParcels.add(it) }
    }
    return idParcels
}

private fun getDetailsApproveSend(getSendPkt: GetSendPkt) {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // val hashmap
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}

private fun notIbTheList(list: ArrayList<String>, element: String): Boolean = element !in list

interface GetPkt {
    fun onGet(value: Array<Parcel>?)
}

interface GetSendPkt {
    fun onGet()
}