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

    private var currentFragment: Fragment? = null
    private var lastBackPressTime: Long = 0

    private lateinit var binding: ActivityLoggedInBinding
    private lateinit var auth: FirebaseAuth

    var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            auth = FirebaseAuth.getInstance()
//            val currentUser = auth.currentUser

            binding = ActivityLoggedInBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
            navView.setupWithNavController(navController)

        } catch (error: Exception) {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            Log.e("ERROR DASHBOARD", error.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

//    override fun onBackPressed() {
//        // Check if the current fragment is the dashboard
//        if (currentFragment is DashboardFragment) {
//            // Check if it's the second click within 2 seconds
//            if (System.currentTimeMillis() - lastBackPressTime < 2000) {
//                // Exit the app
//                finishAffinity()
//            } else {
//                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
//                lastBackPressTime = System.currentTimeMillis()
//            }
//        } else {
//            // If not the dashboard, proceed with normal back press behavior
//            super.onBackPressed()
//        }
//    }

//    private fun setCurrentFragment(fragment: Fragment) {
////        currentFragment = fragment
//    }
}