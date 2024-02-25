package com.example.hygieia_customer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var dialog: AlertDialog
    private lateinit var auth: FirebaseAuth

//    var logTag = "START APPLICATION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val networkViewModel = NetworkViewModel(this)
            networkViewModel.isNetworkAvailable.observe(this) { available ->
                if (available) {
                    val intent = Intent(this, LoggedInActivity::class.java)
                    startActivity(intent)
//                    finish()
                } else {
                    if(!dialog.isShowing)
                        dialog.show()
                }
            }
        }
    }
}