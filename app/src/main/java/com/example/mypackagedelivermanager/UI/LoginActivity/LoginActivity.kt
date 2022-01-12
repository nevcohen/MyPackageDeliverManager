package com.example.mypackagedelivermanager.UI.LoginActivity

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mypackagedelivermanager.Entities.User
import com.example.mypackagedelivermanager.R
import com.example.mypackagedelivermanager.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    var firebaseAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null
    //var REQUEST_READ_PHONE_STATE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val phone = findViewById<EditText>(R.id.textInputEditTextPhone)
        val pinCode = findViewById<EditText>(R.id.textInputEditTextPinCode)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val buttonRegister: AppCompatTextView = findViewById(R.id.textViewLinkRegister)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin: AppCompatButton = findViewById(R.id.appCompatButtonLogin)
        buttonLogin.setOnClickListener {

            val phoneString = phone.text.toString().trim { it <= ' ' }
            val pinCodeString = pinCode.text.toString().trim { it <= ' ' }

            when {
                phoneString.isEmpty() -> {
                    phone.error = "Please provide phone number"
                    phone.requestFocus()
                }
                pinCodeString.isEmpty() -> {
//                    val smsManager = SmsManager.getDefault() as SmsManager
//                    val permissionCheck = ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.READ_PHONE_STATE
//                    )
//
//                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(Manifest.permission.READ_PHONE_STATE),
//                            REQUEST_READ_PHONE_STATE
//                        )
//                    } else {
//                        smsManager.sendTextMessage(phoneString, null, "aaa", null, null)
//                        Toast.makeText(applicationContext, "Message Sent", Toast.LENGTH_LONG).show()
//                    }

                }
                else -> {
                    if (pinCodeString == "0000") {
                        val intent =
                            Intent(applicationContext, MainActivity::class.java)
                        Toast.makeText(
                            applicationContext,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        intent.putExtra("phone", phoneString)
                        startActivity(intent)
                        finish()
                    }
                }
            }

        }
    }
}


