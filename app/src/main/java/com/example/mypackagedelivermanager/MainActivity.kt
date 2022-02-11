package com.example.mypackagedelivermanager

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.mypackagedelivermanager.Entities.User
import com.example.mypackagedelivermanager.UI.LoginActivity.LoginActivity
import com.google.android.material.navigation.NavigationView
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private var fireBaseMU: Firebase_Manager_User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user_key = intent.getStringExtra("user_key")

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navView.getHeaderView(0)
        val nameText: TextView = headerView.findViewById(R.id.user_name)
        val emailText: TextView = headerView.findViewById(R.id.user_email)

        fireBaseMU = Firebase_Manager_User()
        fireBaseMU!!.notifyToParcelList(
            object : Firebase_Manager_User.NotifyDataChange<MutableList<User>> {
                @SuppressLint("SetTextI18n")
                override fun OnDataChanged(obj: MutableList<User>) {
                    for (user in obj)
                        if (user.key == user_key) {
                            nameText.text = user.first_name + " " + user.last_name
                            emailText.text = user.email
                        }
                }

                override fun onFailure(exception: Exception?) {
                    Toast.makeText(
                        this@MainActivity,
                        "error to get users list\n" + exception.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        navView.setNavigationItemSelectedListener {
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.nav_registered -> {
                    fragment = RegisterNavActivity()
                    Toast.makeText(
                        applicationContext,
                        "Clicked Register",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.nav_friends -> {
                    Toast.makeText(
                        applicationContext,
                        "Clicked Friends",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.nav_history -> {
                    fragment = HistoryActivity()
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
                    finish()
                }
            }
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            if (fragment != null) {
                fragmentTransaction.replace(R.id.fragment_container, fragment)
            }
            fragmentTransaction.commit()
            true
        }
    }

    override fun onDestroy() {
        fireBaseMU!!.stopNotifyToStudentList()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}




