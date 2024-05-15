package com.example.hygieia_customer

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_logged_in)
//
//        // Check if the current fragment is StoreProfileFragment
//        if (currentFragment is StoreProfileFragment) {
//            // Navigate to the RewardListFragment using the Navigation Component
//            val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
//            navController.navigate(R.id.offersFragment) // Assuming rewardsFragment is the ID of your RewardsFragment destination
//        } else {
//            super.onBackPressed()
//        }
//    }
}