package com.example.firebaseexample

import android.R
import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseexample.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var storedVerificationId: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.signup.setOnClickListener {
            signup()
        }

        binding.login.setOnClickListener {
            login()
        }


    }

    private fun sendOTP() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("mytag", "onVerificationCompleted:$credential")
                var credentials = PhoneAuthProvider.getCredential(storedVerificationId, "123123")
                signInWithPhoneAuthCredential(credentials)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("mytag", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("mytag", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
            }
        }


        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+8801700507699")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("mytag", "signInWithCredential:success")

                    val user = task.result?.user
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w("mytag", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun signup() {
        var email = binding.userEmail.editText?.text.toString()
        var password = binding.password.editText?.text.toString()

        try{
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    Toast.makeText(this, "called", Toast.LENGTH_SHORT).show()
                    if (task.isSuccessful) {
                        //startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Signup failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: Exception) {
            //Log.d("mytag", e.toString())
        }
    }

    private fun login(){
        try {
            mAuth.signInWithEmailAndPassword(
                binding.userEmail.editText?.text.toString(),
                binding.password.editText?.text.toString())
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        val user = mAuth.currentUser
                        //startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        Toast.makeText(this, "Logged In Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        //Log.d("mytag", "createUserWithEmail:error" + it.exception)
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }catch (e: Exception) {
            Log.d("mytag", e.toString())
        }
    }
}