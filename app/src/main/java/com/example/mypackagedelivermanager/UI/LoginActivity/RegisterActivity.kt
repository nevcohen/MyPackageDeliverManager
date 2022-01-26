package com.example.mypackagedelivermanager.UI.LoginActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.example.mypackagedelivermanager.Entities.User
import com.example.mypackagedelivermanager.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private var mFirebaseAuth: FirebaseAuth? = null
    private var firebaseDatabase: FirebaseDatabase? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var myLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val firstName = findViewById<EditText>(R.id.textInputEditTextFirstName)
        val lastName = findViewById<EditText>(R.id.textInputEditTextLastName)
        val email = findViewById<EditText>(R.id.textInputEditTextEmail)
        val idNum = findViewById<EditText>(R.id.textInputEditTextID)
        val phone = findViewById<EditText>(R.id.textInputEditTextPhone)
        val password = findViewById<EditText>(R.id.textInputEditTextPassword)
        val confirmPassword = findViewById<EditText>(R.id.textInputEditTextConfirmPassword)

        mFirebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

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
            val idNumString = idNum.text.toString().trim { it <= ' ' }
            val phoneString = phone.text.toString().trim { it <= ' ' }
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
                phoneString.isEmpty() -> {
                    phone.error = "Please provide your phone number"
                    phone.requestFocus()
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
                                val currentUser = User(
                                    emailString,
                                    firstNameString,
                                    lastNameString,
                                    myLocation.latitude.toString(),
                                    myLocation.longitude.toString(),
                                    idNumString.toInt(),
                                    phoneString
                                )

                                val uid = task.result!!.user!!.uid
                                currentUser.key = uid
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

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                myLocation = location!!
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                }
            }
            2 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLocation()
                } else {
                    Toast.makeText(this, "Location request was denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}