package com.example.hygieia_customer

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hygieia_customer.databinding.ActivityLoggedInBinding

class LoggedInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            binding = ActivityLoggedInBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

//            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//            val appBarConfiguration = AppBarConfiguration(
//                setOf(
//                R.id.navigation_home, R.id.navigation_transaction,  R.id.navigation_scanQR
//                )
//            )
//            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }
        catch (error: Exception){
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            Log.e("ERROR DASHBOARD", error.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_logged_in)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}