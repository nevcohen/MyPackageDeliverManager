package com.example.mypackagedelivermanager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mypackagedelivermanager.Entities.Parcel
import com.example.mypackagedelivermanager.UI.LoginActivity.LoginActivity
import com.example.mypackagedelivermanager.model.viewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.collections.mapOf as mapOf

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    var firebaseDatabase: FirebaseDatabase = Firebase.database
    var firebaseAuth = FirebaseAuth.getInstance()
//    val email = firbaseAuth.currentUser?.email
  //  val phone = intent.getStringExtra("phone")




    val p1 = Parcel("3", "Envelop", "maor sarusi", "052052", "yes", "4.5", "5.5", "r d ", "MtDC6BqKhXOAqgmAEu4")
    val p2 = Parcel("3", "Envelop", "maor sarusi", "052052", "yes", "4.5", "5.5", "m k ", "MtDI2NIH-Zl-X1K4CG")
    val p3 = Parcel("3", "Envelop", "maor sarusi", "052052", "yes", "4.5", "5.5", "w d", "MtjB41k_JpFBK39nm3W")
    var parcels : List<Parcel> = listOf(p1, p2, p3)
    var p :Array <Parcel>? = null

    //  private lateinit var model: viewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val  headerView  : View = navView.getHeaderView(0)
        val emailText : TextView = headerView.findViewById<TextView>(R.id.user_email)
    //    val email : TextView = findViewById(R.id.emailText)
        val text = emailText.text



        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val addSpinner = findViewById<Spinner>(com.example.mypackagedelivermanager.R.id.pkt_spinner)

        val x :TextView = findViewById(R.id.textParcels)

      val canSendRefRef = firebaseDatabase.getReference("packages by can sends").push()

        val sendButton : Button = findViewById(R.id.finishAndStoreButton)
        sendButton.visibility = View.GONE




        navView.setNavigationItemSelectedListener {
            val sendPack : Switch = findViewById(R.id.avilableSend)
            sendPack.visibility = View.INVISIBLE


            when (it.itemId) {

                R.id.nav_registered ->{

                    Toast.makeText(
                        applicationContext,
                        "Clicked Register",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendPack.text = "want to send this package?"
                    //val ids = fromParcelsToId(parcels)
                  //  parcelSpinnerForRegister(ids, parcels, addSpinner)

                    getAllPackages(object : GetPkt {
                        override fun onGet(value: Array<Parcel>?) {
                            if (value != null) {
                                val idPkt: MutableList<String> = mutableListOf()
                                for (parcel in value)
                                    idPkt.add(parcel.pktId.toString())
                                parcelSpinnerForRegister(idPkt, value.toList(), addSpinner)
                            }
                            p = value
                        }
                    })


                    val packageMap = mutableMapOf<Parcel, ArrayList<String>>()
                    if(p != null){
                        for(parcel in p!!){
                            packageMap[parcel] = java.util.ArrayList()
                        }
                    }

                    sendButton.visibility = View.VISIBLE
                    val test : TextView = findViewById(R.id.test)

                    sendButton.setOnClickListener {



                        if(sendPack.isChecked){
                            val position = addSpinner.selectedItemPosition

                            val testText = test.text
                               if(packageMap[p?.get(position)]?.let{ it1 -> notIbTheList(it1, testText.toString()) } == true)
                        {
                            packageMap[p?.get(position)]?.add(testText.toString())
                            canSendRefRef.setValue(p?.get(position) to packageMap[p?.get(position)])
                        }
                           // if (packageMap[parcels[position]]?.let { it1 -> notIbTheList(it1, testText.toString()) } == true) {
                             //   packageMap[parcels[position]]?.add(testText.toString())
                        //        x.text = packageMap[parcels[position]].toString()

                           // }

                            //canSendRefRef.setValue(parcels[position] to packageMap[parcels[position]])
                        }
                    }
                }


                R.id.nav_friends -> {

                    Toast.makeText(applicationContext, "Clicked Friends", Toast.LENGTH_SHORT).show()
                    parcelSpinnerForFriends(listOf(""))
                }
                R.id.nav_history -> {
                    val x :TextView = findViewById(R.id.textParcels)
                    Toast.makeText(
                    applicationContext,
                    "Clicked History",
                    Toast.LENGTH_SHORT
                ).show()

                }

                R.id.logout -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked Logout",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            true

        }


    }
    private fun parcelSpinnerForRegister(systemPaks: List<String>, parcels : List<Parcel>, addSpinner : Spinner){
        var pos = 0
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val availableSwitch : Switch = findViewById<Switch>(com.example.mypackagedelivermanager.R.id.avilableSend)
        availableSwitch.visibility = View.VISIBLE
        addSpinner.prompt = "Select package by its id"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter
        addSpinner.visibility = View.VISIBLE
        val x  : TextView =  findViewById<TextView>(com.example.mypackagedelivermanager.R.id.textParcels)

        addSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val parcel : Parcel = parcels[position]
                val parcelString = fromParcelToString(parcel)
                addSpinner.setSelection(position)
                x.text = parcelString
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                pktOwnerName = "Unknown"
            }
        }
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
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

    private fun parcelSpinnerForRegister(systemPaks: List<String>) {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val addSpinner = findViewById<Spinner>(R.id.pkt_spinner)
        val availableSwitch : Switch = findViewById<Switch>(R.id.avilableSend)
        availableSwitch.visibility = View.VISIBLE
        addSpinner.prompt = "Select package by its id"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter
        addSpinner.visibility = View.VISIBLE
        val x  : TextView =  findViewById<TextView>(R.id.textParcels)

        addSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                  val parcel : Parcel = parcels[position]
                  val parcelString = fromParcelToString(parcel)
                  addSpinner.setSelection(position)
                  x.text = parcelString




            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                pktOwnerName = "Unknown"
            }
        }
    }

    private fun parcelSpinnerForFriends(systemPaks: List<String>) {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val addSpinner = findViewById<Spinner>(R.id.pkt_spinner)
        addSpinner.prompt = "Select package"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter
        val x  : TextView =  findViewById<TextView>(R.id.textParcels)

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

private fun fromParcelToString(parcel : Parcel): String? = parcel.showParcel()

private fun getParcelById(id : String, parcels : List<Parcel>) : Parcel?
{
    for (parcel in  parcels) {
        if (id == parcel.pktId) return parcel

    }
    return null
}

private fun fromParcelsToId(parcels : List<Parcel>): MutableList<String> {
    val idParcels : MutableList<String> = arrayListOf()
    for (parcel in parcels){
        parcel.pktId?.let {idParcels.add(it)}
    }
    return idParcels
}

private fun getDetailsApproveSend(getSendPkt: GetSendPkt){
    val listener = object : ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
           // val hashmap
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    }
}

private fun notIbTheList(list : ArrayList<String>, element : String) : Boolean = element !in list

interface GetPkt {
    fun onGet(value: Array<Parcel>?)
}
 interface  GetSendPkt{
     fun onGet()
 }




