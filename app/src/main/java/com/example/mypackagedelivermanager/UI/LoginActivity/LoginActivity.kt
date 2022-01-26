package com.example.mypackagedelivermanager.UI.LoginActivity

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.example.mypackagedelivermanager.R
import com.google.firebase.database.FirebaseDatabase
import android.content.pm.PackageManager
import android.os.Build
import com.example.mypackagedelivermanager.Entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthOptions
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.example.mypackagedelivermanager.MainActivity
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue


class LoginActivity : AppCompatActivity() {
    private var firebaseDatabase: FirebaseDatabase? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var verificationId: String? = null
    private var userPhone: String? = null
    private var userKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.SEND_SMS)
                requestPermissions(permissions, 100)
            }
        }

        val phone = findViewById<EditText>(R.id.textInputEditTextPhone)
        val pinCode = findViewById<EditText>(R.id.textInputEditTextPinCode)

        firebaseDatabase = FirebaseDatabase.getInstance()
        mFirebaseAuth = FirebaseAuth.getInstance()

        val buttonRegister: AppCompatTextView = findViewById(R.id.textViewLinkRegister)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin: AppCompatButton = findViewById(R.id.appCompatButtonLogin)
        buttonLogin.setOnClickListener {

            userPhone = phone.text.toString().trim { it <= ' ' }
            val pinCodeString = pinCode.text.toString().trim { it <= ' ' }

            when {
                userPhone!!.isEmpty() -> {
                    phone.error = "Please provide phone number"
                    phone.requestFocus()
                }
                pinCodeString.isEmpty() -> {
                    getUserByPhone(object : GetUser {
                        override fun onGetUserKey(key: String?) {
                            if (key != null) {
                                userKey = key
                                sendVerificationCode(userPhone!!)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "There is no user with this phone number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
                }
                else -> {
                    if (pinCodeString.isEmpty()) {
                        // if the OTP text field is empty display
                        // a message to user to enter OTP
                        Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
                    } else {
                        // if OTP field is not empty calling
                        // method to verify the OTP.
                        verifyCode(pinCodeString)
                    }
                }
            }


        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mFirebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    // if the code is correct and the task is successful
                    // we are sending our user to new activity.
                    val i = Intent(applicationContext, MainActivity::class.java)
                    i.putExtra("user_key", userKey)
                    startActivity(i)
                    finish()
                } else {
                    // if the code is not correct then we are
                    // displaying an error message to the user.
                    Toast.makeText(
                        this@LoginActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun sendVerificationCode(number: String) {
        // this method is used for getting
        // OTP on user phone number.
        val options = PhoneAuthOptions.newBuilder(mFirebaseAuth!!)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // callback method is called on Phone auth provider.
    private val   // initializing our callbacks for on
    // verification callback method.
            mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s
            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val code = phoneAuthCredential.smsCode

                // checking if the code
                // is null or not.
                if (code != null) {
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
                    val pinCode = findViewById<EditText>(R.id.textInputEditTextPinCode)
                    pinCode.setText(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.
                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential)
    }

    private fun getUserByPhone(getCurUser: GetUser) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var curUserKey: String? = null
                for (curUser in dataSnapshot.children) {
                    val user = curUser.getValue<User>()
                    if (user != null && user.phone == userPhone) {
                        curUserKey = user.key
                        break
                    }
                }
                getCurUser.onGetUserKey(curUserKey)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        firebaseDatabase!!.getReference("users").addValueEventListener(userListener)
    }
}

interface GetUser {
    fun onGetUserKey(key: String?)
}


