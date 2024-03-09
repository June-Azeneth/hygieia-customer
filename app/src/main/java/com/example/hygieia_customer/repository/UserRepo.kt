package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepo {
    private val logTag = "UserRepoMessages"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserDetails(userId: String, callback: (UserInfo?) -> Unit) {
        val userRef = fireStore.collection("consumer").document(userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                try {
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(UserInfo::class.java)
                        callback(user)
                    } else {
                        // User document not found
                        callback(null)
                    }
                } catch (error: Exception) {
                    Log.e(logTag, error.toString())
                }
            }
            .addOnFailureListener {
                // Handle errors here (e.g., log, show error message)
                callback(null)
            }

    }

    // Function to get the currently authenticated user
    fun getCurrentUserId(): String? {
        val currentUser = auth.currentUser
        return currentUser?.uid
    }

    fun updateUserProfile(userId: String, updatedUserInfo: UserInfo, callback: (Boolean) -> Unit) {
        val userRef = fireStore.collection("consumer").document(userId)
        val updateData = mapOf(
            "photo" to updatedUserInfo.photo,
            "firstName" to updatedUserInfo.firstName,
            "lastName" to updatedUserInfo.lastName,
            "address" to updatedUserInfo.address
            // Add other fields if needed
        )

        // Update the user document with the specified fields
        userRef.update(updateData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getStoreDetails(storeId: String, callback: (Store?) -> Unit) {
        val userRef = fireStore.collection("store")
            .whereEqualTo("storeId", storeId)

        userRef.get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val storeData = document.data // Retrieve all data from the document
                        val store = Store(
                            document.getString("storeId") ?: "",
                            storeData?.get("address") as? Map<*, *>?,
                            document.getString("email") ?: "",
                            storeData?.get("recyclables") as? List<*>?,
                            document.getString("name") ?: "",
                            document.getString("photo") ?: ""
                        )
                        callback(store)
                    } else {
                        callback(null)
                    }
                } catch (error: Exception) {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}