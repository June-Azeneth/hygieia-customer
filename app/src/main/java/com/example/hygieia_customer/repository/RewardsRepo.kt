package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Reward
import com.google.firebase.firestore.FirebaseFirestore

class RewardsRepo {
    val TAG = "RewardsRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var filterConfig: String = ""

    fun getRewards(callback: (List<Reward>?) -> Unit) {
        firestore.collection("rewards")
            .get()
            .addOnSuccessListener { result ->
                val rewardList = mutableListOf<Reward>()
                for (document in result) {
                    try {
                        val promo = Reward(
                            document.getString("id") ?: "",
                            document.getString("product_name") ?: "",
                            document.getDouble("pts_req") ?: 0.0,
                            document.getString("img_url") ?: "",
                            document.getDouble("discount_rate") ?: 0.0,
                            document.getDouble("disc_price") ?: 0.0,
                            document.getString("storeName") ?: "",
                            )
                        rewardList.add(promo)
                    } catch (error: Exception) {
                        Log.e(TAG, error.toString())
                        callback(null)
                    }
                }
                callback(rewardList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting rewards: ", exception)
                callback(null)
            }
    }
}