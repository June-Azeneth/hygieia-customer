package com.example.hygieia_customer.repository

import com.example.hygieia_customer.model.Ads
import com.example.hygieia_customer.utils.Commons
import com.google.firebase.firestore.FirebaseFirestore

class AdsRepo {
    private val logTag = "ADS"
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userRepo: UserRepo = UserRepo()

    companion object {
        private const val COLLECTION = "ads"
        private const val POSTER = "poster"
        private const val STORE_ID = "storeId"
        private const val TITLE = "title"
        private const val DETAILS = "details"
        private const val IS_DELETED = "isDeleted"
    }

    fun getAllAds(callback: (List<Ads>?) -> Unit) {
        fireStore.collection(COLLECTION)
            .whereEqualTo(IS_DELETED, false)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Commons().log("QUERY SNAPSHOT", querySnapshot.size().toString())
                val ads = mutableListOf<Ads>()
                for (document in querySnapshot) {
                    try {
                        val currentDate = System.currentTimeMillis()
                        val startDate = document.getTimestamp("startDate")?.toDate()?.time ?: 0
                        val endDate = document.getTimestamp("endDate")?.toDate()?.time ?: 0

                        val status = when {
                            currentDate < (startDate) -> "Upcoming"
                            currentDate in (startDate)..(endDate) -> "Ongoing"
                            else -> "Passed"
                        }

                        if (status == "Passed" || status == "Upcoming") {
                            continue
                        }

                        val ad = Ads(
                            document.id,
                            document.getTimestamp("startDate")?.toDate(),
                            document.getTimestamp("endDate")?.toDate(),
                            document.getString(POSTER) ?: "",
                            document.getString(STORE_ID) ?: "",
                            status,
                            document.getString(TITLE) ?: "",
                            document.getString(DETAILS) ?: "",
                        )
                        ads.add(ad)
                    } catch (exception: Exception) {
                        Commons().log("Exception", "Error parsing document: ${exception.message}")
                        callback(null)
                        continue
                    }
                }
                Commons().log("LIST SIZE", ads.size.toString())
                callback(ads)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}