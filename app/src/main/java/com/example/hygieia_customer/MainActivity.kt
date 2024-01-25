package com.example.hygieia_customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    var TAG = "START APPLICATION"
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            var intent = Intent(this, LoggedInActivity::class.java)
            startActivity(intent)
        }

        if (currentUser != null) {
            // The user is signed in
            val uid = currentUser.uid
            val email = currentUser.email

            Log.d(TAG, "User ID: $uid")
            Log.d(TAG, "User Email: $email")
        } else {
            // No user is signed in
            Log.d(TAG, "No user signed in")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
    }
}