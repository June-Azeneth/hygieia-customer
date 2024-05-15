package com.example.hygieia_customer

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.databinding.ActivityMain2Binding
import com.example.hygieia_customer.pages.store.StoreProfileFragment
import com.example.hygieia_customer.pages.store.StoreViewModel
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.SharedViewModel
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView

class MainActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    private val sharedViewModel: SharedViewModel = SharedViewModel()
    private val userRepo: UserRepo = UserRepo()
    private val storeViewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        userRepo.getCurrentUserId()?.let { sharedViewModel.fetchUserDetails(it) }
        sharedViewModel.userDetails.observe(this) { user ->
            val view: android.view.View? = navView.getHeaderView(0)
            val userName: TextView = view?.findViewById(R.id.userName) ?: TextView(this)
            val userEmail: TextView = view?.findViewById(R.id.email) ?: TextView(this)
            val image: ShapeableImageView =
                view?.findViewById(R.id.profile) ?: ShapeableImageView(this)

            userName.text = getString(R.string.userName, user.firstName, user.lastName)
            userEmail.text = user.email

            Glide.with(this)
                .load(user.photo)
                .apply(RequestOptions.centerCropTransform())
                .placeholder(R.drawable.user_photo_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.dashboardFragment,
                R.id.storeListFragment2,
                R.id.transactionFragment,
                R.id.QRScanningFragment,
                R.id.announcementFragment2,
                R.id.offersFragment2,
                R.id.profileFragment2,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (intent.hasExtra("storeId")) {
            val storeId = intent.getStringExtra("storeId") ?: ""
            storeViewModel.setAction("googleMap")
            storeViewModel.setStoreId(storeId)
            Commons().log("STORE ID MAP", storeId)
            val fragment = StoreProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("storeId", storeId)
                }
            }
            supportActionBar?.title = "Store Profile"
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main_activity2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}