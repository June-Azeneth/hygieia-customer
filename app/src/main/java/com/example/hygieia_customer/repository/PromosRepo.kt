package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Promo
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class PromosRepo {
    private val _tag = "PromosRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
        private const val STORE_COLLECTION_NAME = "store"
        private const val STORE_NAME_FIELD = "storeName"
    }

    suspend fun getPromos(callback: (List<Promo>?) -> Unit) = coroutineScope {
        val currentDate = System.currentTimeMillis()
        val promoList = mutableListOf<Promo>()

        try {
            val result = firestore.collection(COLLECTION_NAME).get().await()

            val deferredPromos = result.documents.map { async { processPromoDocument(it, currentDate) } }
            promoList.addAll(deferredPromos.awaitAll().filterNotNull())

            callback(promoList)
            Log.d(_tag, promoList.toString())
        } catch (exception: Exception) {
            Log.e(_tag, "Error getting promos: ", exception)
            callback(null)
        }
    }

    private suspend fun processPromoDocument(document: DocumentSnapshot, currentDate: Long): Promo? {
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

            Log.d(_tag, promoStatus)

            if (promoStatus == "Ongoing" || promoStatus == "Upcoming") {
                val storeName = getStoreDetails(document.getString(STORE_ID) ?: "").await()
                Promo(
                    document.getString(STORE_ID) ?: "",
                    storeName,
                    document.getString(ID) ?: "",
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
            Log.e(_tag, error.toString())
            null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getStoreDetails(storeID: String): Deferred<String> = GlobalScope.async {
        try {
            val document = firestore.collection(STORE_COLLECTION_NAME).document(storeID).get().await()
            document.getString(STORE_NAME_FIELD) ?: ""
        } catch (exception: Exception) {
            Log.e(_tag, "Error getting store details: ", exception)
            ""
        }
    }
}
