package com.example.mypackagedelivermanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mypackagedelivermanager.UI.LoginActivity.LoginActivity
import com.example.mypackagedelivermanager.UI.LoginActivity.RegisterActivity
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

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

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}




