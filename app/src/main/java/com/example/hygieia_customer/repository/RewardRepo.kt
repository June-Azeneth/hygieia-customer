package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Reward
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class RewardRepo {
    private val TAG = "RewardsRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getRewards(callback: (List<Reward>?) -> Unit) {
        try {
            val storeResult = firestore.collection("store").get().await()
            val rewardList = mutableListOf<Reward>()

            coroutineScope {
                val tasks = storeResult.documents.map { storeDocument ->
                    async(Dispatchers.IO) {
                        val storeName = storeDocument.getString("storeID") ?: ""
                        val storeRewardsCollection = storeDocument.reference.collection("reward")

                        try {
                            val rewardResult = storeRewardsCollection.get().await()

                            for (document in rewardResult.documents) {
                                val reward = Reward(
                                    document.getString("id") ?: "",
                                    document.getString("name") ?: "",
                                    document.getDouble("pointsRequired") ?: 0.0,
                                    document.getString("photo") ?: "",
                                    document.getDouble("discount") ?: 0.0,
                                    document.getDouble("discountedPrice") ?: 0.0,
                                    storeName
                                    // Add other fields as needed
                                )
                                rewardList.add(reward)
                            }
                        } catch (exception: Exception) {
                            Log.e(TAG, "Error getting rewards for store $storeName: $exception")
                            callback(null)
                        }
                    }
                }

                tasks.awaitAll()

                // Callback after all tasks are completed
                callback(rewardList)
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Error getting stores: $exception")
            callback(null)
        }
    }
}