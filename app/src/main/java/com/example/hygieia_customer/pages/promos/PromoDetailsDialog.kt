package com.example.hygieia_customer.pages.promos

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.utils.Commons
import com.google.android.material.imageview.ShapeableImageView

class PromoDetailsDialog(
    context: Context,
    private val promo: Promo,
    private val navController: NavController
) : Dialog(context) {
    private lateinit var photo: ShapeableImageView
    private lateinit var product: TextView
    private lateinit var originalPrice: TextView
    private lateinit var discountedPrice: TextView
    private lateinit var shop: TextView
    private lateinit var points: TextView
    private lateinit var description: TextView
    private lateinit var discount: TextView
    private lateinit var promoName: TextView
    private lateinit var duration: TextView
    private lateinit var visitShop: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_promo_details_dialog)
        initializeVariables()
        setUpUi()
    }

    private fun initializeVariables() {
        visitShop = findViewById(R.id.visitShop)
        discount = findViewById(R.id.discount)
        description = findViewById(R.id.description)
        points = findViewById(R.id.points)
        shop = findViewById(R.id.shop)
        discountedPrice = findViewById(R.id.discountedPrice)
        originalPrice = findViewById(R.id.originalPrice)
        product = findViewById(R.id.product)
        photo = findViewById(R.id.photo)
        promoName = findViewById(R.id.promoName)
        duration = findViewById(R.id.duration)

        visitShop.setOnClickListener {
            navController.navigate(R.id.to_store_profile)
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUi() {
        if (promo.discountRate.toInt() == 100) {
            discount.text = "FREE"
        } else {
            discount.text = "-${Commons().formatDecimalNumber(promo.discountRate)}%"
        }

        if (promo.description.isEmpty()) {
            description.text = "Description: None"
        } else {
            description.text = "Description: \n${promo.description}"
        }
        promoName.text = promo.promoName
        duration.text = "Duration: ${promo.dateStart?.let { Commons().dateFormatMMMDD(it) }} to ${
            promo.dateEnd?.let {
                Commons().dateFormatMMMDD(it)
            }
        }"
        points.text = "Points Required: ${Commons().formatDecimalNumber(promo.pointsRequired)} pts"
        shop.text = "Shop: ${promo.storeName}"
        discountedPrice.text =
            "Discounted Price: ₱${Commons().formatDecimalNumber(promo.discountedPrice)}"
        originalPrice.text = "Original Price: ₱${Commons().formatDecimalNumber(promo.price)}"
        product.text = "Product: ${promo.product}"
        Glide.with(context).load(promo.photo).into(photo)
    }
}