package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class RewardsRepo {
    val TAG = "RewardsRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_NAME = "reward"
        private const val STORE_ID = "storeId"
        private const val ID = "id"
        private const val PHOTO = "photo"
        private const val NAME = "name"
        private const val DISCOUNT_RATE = "discountRate"
        private const val DISCOUNTED_PRICE = "discountedPrice"
        private const val POINTS_REQUIRED = "pointsRequired"
        private const val STORE_NAME = "storeName"
    }

    fun getRewards(callback: (List<Reward>?) -> Unit) {
        firestore.collection(COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                val rewardList = mutableListOf<Reward>()
                for (document in result) {
                    try {
                        val promo = Reward(
                            document.getString(ID) ?: "",
                            document.getString(NAME) ?: "",
                            document.getDouble(POINTS_REQUIRED) ?: 0.0,
                            document.getString(PHOTO) ?: "",
                            document.getDouble(DISCOUNT_RATE) ?: 0.0,
                            document.getDouble(DISCOUNTED_PRICE) ?: 0.0,
                            document.getString(STORE_NAME) ?: "",
                            document.getString(STORE_ID) ?: ""
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

    fun getRewardByStoreId(storeId: String, callback: (List<Reward>?) -> Unit){
        firestore.collection(COLLECTION_NAME)
            .whereEqualTo(STORE_ID, storeId)
            .get()
            .addOnSuccessListener { result ->
                val rewardList = mutableListOf<Reward>()
                for (document in result) {
                    try {
                        val promo = Reward(
                            document.getString("id") ?: "",
                            document.getString("name") ?: "",
                            document.getDouble("pointsRequired") ?: 0.0,
                            document.getString("photo") ?: "",
                            document.getDouble("discount") ?: 0.0,
                            document.getDouble("discountedPrice") ?: 0.0,
                            document.getString("storeName") ?: "",
                            document.getString("storeId") ?: ""
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