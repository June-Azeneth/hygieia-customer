package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore

class TransactionRepo {
    val TAG = "TransactionRepoMessages"
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getTransactions(userId: String, callback: (List<Transaction>?) -> Unit) {

        firestore.collection("transaction")
            .whereEqualTo("customerID", userId)
            .get()
            .addOnSuccessListener { result ->
                val promoList = mutableListOf<Transaction>()
                for (document in result) {
                    try {
                        val transaction = Transaction(
                            document.getString("customerID") ?: "",
                            document.getString("customerName") ?: "",
                            document.getString("date") ?: "",
                            document.getDouble("discount") ?: 0.0,
                            document.getDouble("points_earned") ?: 0.0,
                            document.getDouble("points_spent") ?: 0.0,
                            document.getString("product") ?: "",
                            document.getString("storeName") ?: "",
                            document.getDouble("total") ?: 0.0,
                            document.getString("type") ?: "",
                        )
                        promoList.add(transaction)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error creating Transaction object: ${e.message}")
                        // Handle the error or skip this document
                    }
                }
                callback(promoList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting transactions: ", exception)
                callback(null)
            }
    }

    //For a more accurate data, we should query the product and store name as well
    //TO DO query product and store name before passing
}