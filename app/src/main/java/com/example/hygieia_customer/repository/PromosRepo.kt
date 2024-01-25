package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Promo
import com.google.firebase.firestore.FirebaseFirestore

class PromosRepo {
    val TAG = "PromosRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var filterConfig: String = ""

    fun getPromos(callback: (List<Promo>?) -> Unit) {
        firestore.collection("promo")
            .get()
            .addOnSuccessListener { result ->
                val promoList = mutableListOf<Promo>()
                for (document in result) {
                    try {
                        val promo = Promo(
                            document.getString("id") ?: "",
                            document.getString("name") ?: "",
                            document.getString("product") ?: "",
                            document.getString("promo_end") ?: "Available Indefinitely",
                            document.getDouble("pts_req") ?: 0.0,
                            document.getString("img_url") ?: "",
                            document.getDouble("discount") ?: 0.0,
                            document.getDouble("disc_price") ?: 0.0,
                            document.getString("storeName") ?: "",
                        )
                        promoList.add(promo)
                    } catch (error: Exception) {
                        Log.e(TAG, error.toString())
                        callback(null)
                    }
                }
                callback(promoList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting promos: ", exception)
                callback(null)
            }
    }
}