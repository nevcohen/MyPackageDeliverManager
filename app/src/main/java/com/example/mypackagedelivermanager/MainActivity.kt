package com.example.mypackagedelivermanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mypackagedelivermanager.Entities.Parcel
import com.example.mypackagedelivermanager.UI.LoginActivity.LoginActivity
import com.example.mypackagedelivermanager.UI.LoginActivity.RegisterActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    var firebaseDatabase: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_registered -> Toast.makeText(
                    applicationContext,
                    "Clicked Register",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_friends -> Toast.makeText(
                    applicationContext,
                    "Clicked Friends",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_history -> Toast.makeText(
                    applicationContext,
                    "Clicked History",
                    Toast.LENGTH_SHORT
                ).show()
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

        getAllPackages(object : GetPkt {
            override fun onGet(value: Array<Parcel>?) {
                if (value != null) {
                    val idPkt: MutableList<String> = mutableListOf()
                    for (parcel in value)
                        parcel.pktId?.let { idPkt.add(it) }
                    parcelSpinner(idPkt.toTypedArray())
                }
            }
        })

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
                    curPkt.getValue<Parcel>()?.let { systemPkts.add(it) }
                }
                getNameOfPkt.onGet(systemPkts.toTypedArray())
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        firebaseDatabase!!.getReference("packages").addValueEventListener(pktListener)
    }

    private fun parcelSpinner(systemPaks: Array<String>) {
        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, systemPaks)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val addSpinner = findViewById<Spinner>(R.id.pkt_spinner)
        addSpinner.prompt = "Select package"
        addSpinner.setSelection(0, false)
        addSpinner.adapter = arrayAdapter

        addSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
//                    pktOwnerName = systemUsers[position]
//                    addSpinner.setSelection(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
//                pktOwnerName = "Unknown"
            }
        }
    }
}

interface GetPkt {
    fun onGet(value: Array<Parcel>?)
}




