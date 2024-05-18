package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.RedeemedRewards
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.utils.Commons
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionsRepo {
    private val logTag = "TransactionRepoMessages"
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    //COLLECTION BASED QUERY
//    suspend fun getTransactions(userId: String, callback: (List<Transaction>?) -> Unit) {
//        return withContext(Dispatchers.IO) {
//            val result = fireStore.collection("store").get().await()
//            fireStore.collection("transaction")
//                .whereEqualTo("customerId", userId)
//                .orderBy("addedOn", Query.Direction.DESCENDING)
//                .get()
//                .addOnSuccessListener { result ->
//                    val transactionList = mutableListOf<Transaction>()
//                    for (document in result) {
//                        try {
//                            val transaction = Transaction(
//                                document.id,
//                                "",
//                                document.getString("customerId") ?: "",
//                                document.getString("type") ?: "",
//                                document.getString("rewardId") ?: "",
//                                document.getTimestamp("addedOn")?.toDate(),
//                                document.getDouble("pointsSpent") ?: 0.0,
//                                document.getDouble("pointsEarned") ?: 0.0,
//                                document.getDouble("total") ?: 0.0,
//                                document.getString("product") ?: "",
//                                document.getString("promoName") ?: "",
//                            )
//                            val storeName = getStoreName(reward.storeId)
//                            if (storeName != null) {
//                                reward.storeName = storeName
//                            }
//
//                            transactionList.add(transaction)
//                        } catch (e: Exception) {
//                            Log.e(logTag, "Error creating Transaction object: ${e.message}")
//                            // Handle the error or skip this document
//                        }
//                    }
//                    callback(transactionList)
//                    Log.e(logTag, transactionList.toString())
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(logTag, "Error getting transactions: ", exception)
//                    callback(null)
//                }
//        }
//    }

    suspend fun getTransactions(): List<Transaction>? {
        return withContext(Dispatchers.IO) {
            currentUser?.let { user ->
                val query = fireStore.collection("transaction")
                    .whereEqualTo("customerId", user.uid)
                    .orderBy("addedOn", Query.Direction.DESCENDING)

                try {
                    val result = query.get().await()
                    val transactionList = mutableListOf<Transaction>()
                    for (document in result) {
                        try {
                            val transaction = Transaction(
                                document.id,
                                "",
                                document.getString("customerId") ?: "",
                                document.getString("type") ?: "",
                                document.getString("rewardId") ?: "",
                                document.getTimestamp("addedOn")?.toDate(),
                                document.getDouble("pointsSpent") ?: 0.0,
                                document.getDouble("pointsEarned") ?: 0.0,
                                document.getDouble("total") ?: 0.0,
                                document.getString("product") ?: "",
                                document.getString("promoName") ?: "",
                                document.getString("storeId") ?: "",
                            )
                            val storeName = getStoreName(transaction.storeId)
                            if (storeName != null) {
                                transaction.storeName = storeName
                            }
                            transactionList.add(transaction)
                        } catch (error: Error) {
                            Commons().log("TRANSACTION_REPO", "${error.message}")
                        }
                    }
                    transactionList
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun getTransactionProducts(transactionId: String): List<RedeemedRewards>? {
        return withContext(Dispatchers.IO) {
            try {
                val fireStore = FirebaseFirestore.getInstance()
                val productsRef = fireStore.collection("transaction").document(transactionId)
                    .collection("products")

                val querySnapshot = productsRef.get().await()
                val productsList = mutableListOf<RedeemedRewards>()
                for (document in querySnapshot) {
                    val productData = document.toObject(RedeemedRewards::class.java)
                    productsList.add(productData)
                }
                productsList
            } catch (e: Exception) {
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
    //For a more accurate data, we should query the product and store name as well
    //TO DO query product and store name before passing
}