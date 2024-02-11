package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TransactionsRepo {
    val TAG = "TransactionRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    //COLLECTION BASED QUERY
    fun getTransactions(userId: String, callback: (List<Transaction>?) -> Unit) {
        firestore.collection("transaction")
            .whereEqualTo("customerID", userId)
            .orderBy("addedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val transactionList = mutableListOf<Transaction>()
                for (document in result) {
                    try {
                        val transaction = Transaction(
                            document.getString("id") ?: "",
                            document.getString("customerID") ?: "",
                            document.getString("customerName") ?: "",
                            document.getDouble("discount") ?: 0.0,
                            document.getDouble("pointsEarned") ?: 0.0,
                            document.getDouble("pointsSpent") ?: 0.0,
                            document.getString("product") ?: "",
                            document.getString("storeName") ?: "",
                            document.getDouble("total") ?: 0.0,
                            document.getTimestamp("addedOn"),
                            document.getString("type") ?: ""
                        )
                        transactionList.add(transaction)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error creating Transaction object: ${e.message}")
                        // Handle the error or skip this document
                    }
                }
                callback(transactionList)
                Log.e(TAG, transactionList.toString())
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting transactions: ", exception)
                callback(null)
            }
    }

    //For a more accurate data, we should query the product and store name as well
    //TO DO query product and store name before passing
}