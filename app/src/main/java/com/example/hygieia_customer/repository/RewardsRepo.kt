package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.model.Store
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RewardsRepo {
    private val logTag = "RewardsRepoMessages"
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_NAME = "reward"
        private const val STORE_ID = "storeId"
        private const val ID = "id"
        private const val PHOTO = "photo"
        private const val NAME = "name"
        private const val DISCOUNT_RATE = "discount"
        private const val DISCOUNTED_PRICE = "discountedPrice"
        private const val PRICE = "price"
        private const val POINTS_REQUIRED = "pointsRequired"
        private const val STORE_NAME = "storeName"
        private const val DESCRIPTION = "description"
        private const val STATUS = "status"
    }

    suspend fun searchReward(rewardName: String, callback: (List<Reward>?) -> Unit) {
        try {
            val querySnapshot = fireStore.collection("reward")
                .whereEqualTo("status", "active")
                .get()
                .await()

            val rewardList = mutableListOf<Reward>()
            for (document in querySnapshot.documents) {
                try {
                    val name = document.getString("name") ?: ""
                    if (name.contains(rewardName, ignoreCase = true)) {
                        val reward = Reward(
                            document.getString(ID) ?: "",
                            document.getString(NAME) ?: "",
                            document.getDouble(POINTS_REQUIRED) ?: 0.0,
                            document.getString(PHOTO) ?: "",
                            document.getDouble(DISCOUNT_RATE) ?: 0.0,
                            document.getDouble(DISCOUNTED_PRICE) ?: 0.0,
                            document.getString(STORE_NAME) ?: "",
                            document.getString(STORE_ID) ?: "",
                            document.getDouble(PRICE) ?: 0.0,
                            document.getString(DESCRIPTION) ?: "",
                        )
                        val storeName = getStoreName(reward.storeId)
                        if (storeName != null) {
                            reward.storeName = storeName
                        }
                        rewardList.add(reward)
                    }
                } catch (error: Exception) {
                    callback(null)
                }
            }
            callback(rewardList)
        } catch (e: Exception) {
            callback(null)
        }
    }

    suspend fun getRewards(): List<Reward>? {
        return withContext(Dispatchers.IO) {
            try {
                val result = fireStore.collection(COLLECTION_NAME)
                    .whereEqualTo(STATUS, "active")
                    .get().await()
                val rewardList = mutableListOf<Reward>()
                for (document in result) {
                    try {
                        val reward = Reward(
                            document.getString(ID) ?: "",
                            document.getString(NAME) ?: "",
                            document.getDouble(POINTS_REQUIRED) ?: 0.0,
                            document.getString(PHOTO) ?: "",
                            document.getDouble(DISCOUNT_RATE) ?: 0.0,
                            document.getDouble(DISCOUNTED_PRICE) ?: 0.0,
                            document.getString(STORE_NAME) ?: "",
                            document.getString(STORE_ID) ?: "",
                            document.getDouble(PRICE) ?: 0.0,
                            document.getString(DESCRIPTION) ?: "",
                        )
                        val storeName = getStoreName(reward.storeId)
                        if (storeName != null) {
                            reward.storeName = storeName
                        }
                        rewardList.add(reward)
                    } catch (error: Exception) {
                        Log.e(logTag, "Error parsing reward document: ${error.message}")
                    }
                }
                rewardList
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching rewards: ${e.message}")
                null
            }
        }
    }

    private suspend fun getStoreName(id: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val querySnapshot = fireStore.collection("store")
                    .whereEqualTo("storeId", id)
                    .whereEqualTo("status", "active")
                    .get().await()
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first()
                    document.getString("name")
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e(logTag, "Error fetching store name: ${e.message}")
                null
            }
        }
    }

    fun getRewardByStoreId(storeId: String, callback: (List<Reward>?) -> Unit) {
        fireStore.collection(COLLECTION_NAME)
            .whereEqualTo(STORE_ID, storeId)
            .whereEqualTo(STATUS, "active")
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
                            document.getString("storeId") ?: "",
                            document.getDouble("price") ?: 0.0,
                        )
                        rewardList.add(promo)
                    } catch (error: Exception) {
                        Log.e(logTag, error.toString())
                        callback(null)
                    }
                }
                callback(rewardList)
            }
            .addOnFailureListener { exception ->
                Log.w(logTag, "Error getting rewards: ", exception)
                callback(null)
            }
    }
}