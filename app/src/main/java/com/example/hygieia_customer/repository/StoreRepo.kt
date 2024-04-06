package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Store
import com.google.firebase.firestore.FirebaseFirestore

class StoreRepo {
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    interface StoreFetchCallback {
        fun onStoresFetched(stores: List<Store>?)
        fun onError(error: String)
    }

//    fun getStores(callback: StoreFetchCallback) {
//        try {
//            fireStore.collection("store")
//                .get()
//                .addOnSuccessListener { querySnapshot ->
//                    val storeList = mutableListOf<Store>()
//                    for (document in querySnapshot.documents) {
//                        val store = document.toObject(Store::class.java)
//                        store?.let {
//                            storeList.add(it)
//                        }
//                    }
//                    Commons().log("STORE REPO", storeList.size.toString())
//                    callback.onStoresFetched(storeList)
//                }
//                .addOnFailureListener { e ->
//                    callback.onError("Error fetching stores: ${e.message}")
//                }
//        } catch (e: Exception) {
//            callback.onError("Error fetching stores: ${e.message}")
//        }
//    }

    fun searchStore(storeName: String, callback: (List<Store>?) -> Unit) {
        val queryRef = fireStore.collection("store")
            .whereEqualTo("status", "active")

        queryRef.get()
            .addOnSuccessListener { querySnapshot ->
                val storeList = mutableListOf<Store>()
                for (document in querySnapshot.documents) {
                    try {
                        val storeData = document.data
                        val name = document.getString("name") ?: ""
                        if (name.contains(storeName, ignoreCase = true)) {
                            val store = Store(
                                document.getString("storeId") ?: "",
                                storeData?.get("address") as? Map<*, *>,
                                document.getString("email") ?: "",
                                storeData?.get("recyclable") as? List<*>,
                                name,
                                document.getString("photo") ?: ""
                            )
                            storeList.add(store)
                        }
                    } catch (error: Exception) {
                        callback(null)
                    }
                }
                callback(storeList)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getStores(callback: (List<Store>?) -> Unit) {
        try {
            fireStore.collection("store")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val storeList = mutableListOf<Store>()
                    for (document in querySnapshot) {
                        try {
                            val storeData = document.data
                            val store = Store(
                                document.getString("storeId") ?: "",
                                storeData["address"] as? Map<*, *>?,
                                document.getString("email") ?: "",
                                storeData["recyclable"] as? List<*>?,
                                document.getString("name") ?: "",
                                document.getString("photo") ?: "",
                            )
                            storeList.add(store)
                        } catch (error: Exception) {
                            callback(null)
                        }
                    }
                    callback(storeList)
                }
                .addOnFailureListener {
                    callback(null)
                }
        } catch (e: Exception) {
            Log.e("STORE_REPO", "Error fetching stores: ${e.message}")
            callback(null)
        }
    }
}