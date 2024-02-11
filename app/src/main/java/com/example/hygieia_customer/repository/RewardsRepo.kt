package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Reward
import com.google.firebase.firestore.FirebaseFirestore

class RewardsRepo {
    val TAG = "RewardsRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //Collection based query
    fun getRewards(callback: (List<Reward>?) -> Unit) {
        firestore.collection("reward")
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

    //sub-collection based query
//    fun getRewards(callback: (List<Reward>?) -> Unit) {
//        val allRewards = mutableListOf<Reward>()
//
//        firestore.collectionGroup("reward")
//            .get()
//            .addOnSuccessListener { querySnapshot ->
//                for (rewardDoc in querySnapshot.documents) {
//                    val rewardData = rewardDoc.toObject(Reward::class.java)
//                    if (rewardData != null) {
//                        allRewards.add(rewardData)
//                    }
//                }
//                callback(allRewards)
//            }
//            .addOnFailureListener { exception ->
//                println("Error getting rewards documents: $exception")
//                callback(null)
//            }
//    }
}