package com.example.hygieia_customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
//            .setView(R.layout.connectivity_dialog_box)
//            .setCancelable(true)
//            .create()
//
//        auth = Firebase.auth
//        val currentUser = auth.currentUser
//
//        if (currentUser != null) {
//            val networkViewModel = NetworkViewModel(this)
//            networkViewModel.isNetworkAvailable.observe(this) { available ->
//                if (available) {
//                    val intent = Intent(this, LoggedInActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                } else {
//                    if(!dialog.isShowing)
//                        dialog.show()
//                }
//            }
//        }
    }
}