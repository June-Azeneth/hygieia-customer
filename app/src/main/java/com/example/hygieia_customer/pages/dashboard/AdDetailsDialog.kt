package com.example.hygieia_customer.pages.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Ads
import com.example.hygieia_customer.model.Reward
import com.google.android.material.imageview.ShapeableImageView

class AdDetailsDialog(
    context: Context,
    private val ad: Ads,
    private val navController: NavController
) : Dialog(context) {

    private lateinit var photo: ShapeableImageView
    private lateinit var title: TextView
    private lateinit var details: TextView
    private lateinit var visitShop: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_ad_detaisl_dialog)
        initializeVariables()
        setUpUi()
    }

    private fun setUpUi() {
        Glide.with(context).load(ad.poster).into(photo)
        title.text = ad.title
        details.text = ad.details
        title.text = ad.title
        visitShop.setOnClickListener {
            navController.navigate(R.id.to_store_profile)
            dismiss()
        }
    }

    private fun initializeVariables() {
        visitShop = findViewById(R.id.visitShop)
        title = findViewById(R.id.title)
        details = findViewById(R.id.details)
        photo = findViewById(R.id.photo)
    }
}