package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.model.Reward
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PromosRepo {
    private val logTag = "PromosRepoMessages"
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        private const val COLLECTION_NAME = "promo"
        private const val STORE_ID = "storeId"
        private const val ID = "id"
        private const val PRODUCT = "product"
        private const val PHOTO = "photo"
        private const val NAME = "name"
        private const val PRICE = "discountedPrice"
        private const val DISCOUNT_RATE = "discountRate"
        private const val POINTS_REQUIRED = "pointsRequired"
        private const val PROMO_START = "promoStart"
        private const val PROMO_END = "promoEnd"
        private const val DATE_PAUSED = "datePaused"
        private const val DATE_RESUME = "dateResume"
        private const val STORE_NAME = "storeName"
    }

//    suspend fun getPromos(callback: (List<Promo>?) -> Unit) = coroutineScope {
//        val currentDate = System.currentTimeMillis()
//        val promoList = mutableListOf<Promo>()
//
//        try {
//            val result = fireStore.collection(COLLECTION_NAME).get().await()
//
//            val deferredPromos = result.documents.map { async { processPromoDocument(it, currentDate) } }
//            promoList.addAll(deferredPromos.awaitAll().filterNotNull())
//
//            callback(promoList)
//            Log.d(logTag, promoList.toString())
//        } catch (exception: Exception) {
//            Log.e(logTag, "Error getting promos: ", exception)
//            callback(null)
//        }
//    }

    suspend fun getPromos(): List<Promo>? {
        return withContext(Dispatchers.IO) {
            try {
                val result = fireStore.collection(COLLECTION_NAME).get().await()
                val rewardList = mutableListOf<Promo>()
                for (document in result) {
                    try {
                        val reward = Promo(
                            document.id,
                            document.getString(STORE_ID) ?: "",
                            document.getString(PRODUCT) ?: "",
                            document.getString(PHOTO) ?: "",
                            document.getString(NAME) ?: "",
                            document.getDouble(PRICE) ?: 0.0,
                            document.getDouble(DISCOUNT_RATE) ?: 0.0,
                            document.getDouble(POINTS_REQUIRED) ?: 0.0,
                            document.getTimestamp(PROMO_START)?.toDate(),
                            document.getTimestamp(PROMO_END)?.toDate(),
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
                val querySnapshot = fireStore.collection("store").whereEqualTo("storeId", id).get().await()
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

    private fun processPromoDocument(document: DocumentSnapshot, currentDate: Long): Promo? {
        return try {
            val startDate = document.getTimestamp(PROMO_START)?.toDate()?.time ?: 0
            val endDate = document.getTimestamp(PROMO_END)?.toDate()?.time ?: 0
            val pausedDate = document.getTimestamp(DATE_PAUSED)?.toDate()?.time ?: 0
            val resumeDate = document.getTimestamp(DATE_RESUME)?.toDate()?.time ?: 0

            val promoStatus = when {
                currentDate < startDate -> "Upcoming"
                currentDate in startDate..endDate ->
                    if (currentDate in pausedDate..resumeDate) "Paused" else "Ongoing"
                else -> "Passed"
            }

            Log.d(logTag, promoStatus)

            if (promoStatus == "Ongoing" || promoStatus == "Upcoming") {
                Promo(
                    document.id,
                    document.getString(STORE_ID) ?: "",
                    document.getString(PRODUCT) ?: "",
                    document.getString(PHOTO) ?: "",
                    document.getString(NAME) ?: "",
                    document.getDouble(PRICE) ?: 0.0,
                    document.getDouble(DISCOUNT_RATE) ?: 0.0,
                    document.getDouble(POINTS_REQUIRED) ?: 0.0,
                    document.getTimestamp(PROMO_START)?.toDate(),
                    document.getTimestamp(PROMO_END)?.toDate(),
                )
            } else {
                null
            }
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
            null
        }
    }

    fun getPromosByStoreId(storeId: String, callback: (List<Promo>?) -> Unit) {
        val currentDate = System.currentTimeMillis()
        fireStore.collection(COLLECTION_NAME)
            .whereEqualTo(STORE_ID, storeId)
            .get()
            .addOnSuccessListener { result ->
                val rewardList = mutableListOf<Promo>()
                for (document in result) {
                    try {
                        val startDate = document.getTimestamp(PROMO_START)?.toDate()?.time ?: 0
                        val endDate = document.getTimestamp(PROMO_END)?.toDate()?.time ?: 0
                        val pausedDate = document.getTimestamp(DATE_PAUSED)?.toDate()?.time ?: 0
                        val resumeDate = document.getTimestamp(DATE_RESUME)?.toDate()?.time ?: 0

                        val promoStatus = when {
                            currentDate < startDate -> "Upcoming"
                            currentDate in startDate..endDate ->
                                if (currentDate in pausedDate..resumeDate) "Paused" else "Ongoing"

                            else -> "Passed"
                        }

                        if (promoStatus == "Ongoing" || promoStatus == "Upcoming") {
                            val promo = Promo(
                                document.id,
                                document.getString(STORE_ID) ?: "",
                                document.getString(PRODUCT) ?: "",
                                document.getString(PHOTO) ?: "",
                                document.getString(NAME) ?: "",
                                document.getDouble(PRICE) ?: 0.0,
                                document.getDouble(DISCOUNT_RATE) ?: 0.0,
                                document.getDouble(POINTS_REQUIRED) ?: 0.0,
                                document.getTimestamp(PROMO_START)?.toDate(),
                                document.getTimestamp(PROMO_END)?.toDate(),
                            )
                            rewardList.add(promo)
                        }

                    } catch (error: Exception) {
                        callback(null)
                    }
                }
                callback(rewardList)
            }
            .addOnFailureListener { exception ->
                callback(null)
                Log.e("PROMOSREPO", exception.toString())
            }
    }
}
