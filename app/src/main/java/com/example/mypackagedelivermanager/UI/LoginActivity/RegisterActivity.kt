package com.example.mypackagedelivermanager.UI.LoginActivity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.example.mypackagedelivermanager.Entities.UserParcel
import com.example.mypackagedelivermanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    var mFirebaseAuth: FirebaseAuth? = null
    var firebaseDatabase: FirebaseDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val firstName = findViewById<EditText>(R.id.textInputEditTextFirstName)
        val lastName = findViewById<EditText>(R.id.textInputEditTextLastName)
        val email = findViewById<EditText>(R.id.textInputEditTextEmail)
        val address = findViewById<EditText>(R.id.textInputEditTextAddress)
        val idNum = findViewById<EditText>(R.id.textInputEditTextID)
        val password = findViewById<EditText>(R.id.textInputEditTextPassword)
        val confirmPassword = findViewById<EditText>(R.id.textInputEditTextConfirmPassword)

        mFirebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()

        val buttonLogin: AppCompatTextView = findViewById(R.id.appCompatTextViewLoginLink)
        buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val regButton: AppCompatButton = findViewById(R.id.appCompatButtonRegister)
        regButton.setOnClickListener {

            val firstNameString = firstName.text.toString().trim { it <= ' ' }
            val lastNameString = lastName.text.toString().trim { it <= ' ' }
            val emailString = email.text.toString().trim { it <= ' ' }
            val addressString = address.text.toString().trim { it <= ' ' }
            val idNumString = idNum.text.toString().trim { it <= ' ' }
            val passwordString = password.text.toString().trim { it <= ' ' }
            val confirmPasswordString = confirmPassword.text.toString().trim { it <= ' ' }

            when {
                firstNameString.isEmpty() -> {
                    firstName.error = "Please provide your first name"
                    firstName.requestFocus()
                }
                lastNameString.isEmpty() -> {
                    lastName.error = "Please provide your last name"
                    lastName.requestFocus()
                }
                emailString.isEmpty() -> {
                    email.error = "Please provide email id"
                    email.requestFocus()
                }
                addressString.isEmpty() -> {
                    address.error = "Please provide your address"
                    address.requestFocus()
                }
                idNumString.isEmpty() -> {
                    idNum.error = "Please provide your id"
                    idNum.requestFocus()
                }
                idNumString.length != 9 -> {
                    idNum.error = "Please provide 9-digit id"
                    idNum.requestFocus()
                }
                !idValidator(idNumString) -> {
                    idNum.error = "Invalid ID"
                    idNum.requestFocus()
                }
                passwordString.isEmpty() -> {
                    password.error = "Please provide password"
                    password.requestFocus()
                }
                confirmPasswordString.isEmpty() -> {
                    confirmPassword.error = "Please provide confirm password"
                    confirmPassword.requestFocus()
                }
                passwordString != confirmPasswordString -> {
                    confirmPassword.error = "Please provide identical Passwords"
                    confirmPassword.requestFocus()
                }
                else -> {
                    mFirebaseAuth!!.createUserWithEmailAndPassword(emailString, passwordString)
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "SignUp Unsuccessful, please Try Again!" +
                                            task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val currentUser = insertUserIntoFireBase(
                                    addressString,
                                    emailString,
                                    firstNameString,
                                    lastNameString,
                                    idNumString.toInt(),
                                )

                                val uid = task.result!!.user!!.uid
                                firebaseDatabase!!.getReference("users").child(uid)
                                    .setValue(currentUser)
                                    .addOnSuccessListener {
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "You have successfully registered",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        }

                    mFirebaseAuth!!.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun idValidator(id: String): Boolean {
        var sum = 0
        for (i in 0..8) {
            val incNum = id[i].digitToInt() * ((i % 2) + 1)
            sum += incNum
            if (incNum > 9)
                sum -= 9
        }
        return (sum % 10 == 0)
    }

    private fun insertUserIntoFireBase(
        address: String,
        email: String,
        first_name: String,
        last_name: String,
        user_id: Int,
    ): UserParcel {
        return UserParcel(address, email, first_name, last_name, user_id)
    }
}