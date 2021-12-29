package com.example.mypackagedelivermanager.UI.LoginActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.mypackagedelivermanager.R
import com.example.mypackagedelivermanager.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val buttonRegister: Button = findViewById(R.id.openRegisterActivity)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val email = findViewById<EditText>(R.id.emailLogin)
        val phoneNum = findViewById<EditText>(R.id.phoneNumberLogin)
        val password = findViewById<EditText>(R.id.passwordLogin)
        val button1: Button = findViewById(R.id.login)
        button1.setOnClickListener {

            val emailString = email.text.toString().trim { it <= ' ' }
            val passwordString = password.text.toString().trim { it <= ' ' }
            val phoneNumString = phoneNum.text.toString().trim { it <= ' ' }
            val database = Firebase.database

            if (!((emailString == "") || passwordString == "" || phoneNumString == "")) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            Toast.makeText(
                                this,
                                "you are login successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }

    }
}