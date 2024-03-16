package com.example.hygieia_customer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hygieia_customer.databinding.ActivityLoggedInBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class LoggedInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggedInBinding
    private lateinit var auth: FirebaseAuth

    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            auth = FirebaseAuth.getInstance()
            binding = ActivityLoggedInBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
            navView.setupWithNavController(navController)

        } catch (error: Exception) {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}