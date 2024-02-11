package com.example.hygieia_customer.repository

import android.util.Log
import com.example.hygieia_customer.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepo {
    val TAG = "UserRepoMessages"
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getUserDetails(userId: String, callback: (UserInfo?) -> Unit) {
        val userRef = firestore.collection("consumer").document(userId)
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
                    Log.e(TAG, error.toString())
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
        val userRef = firestore.collection("consumer").document(userId)

        // Create a map with only the fields you want to update
//        val updateData = mapOf(
//            "user_photo" to updatedUserInfo.userPhoto,
//            "first_name" to updatedUserInfo.firstName,
//            "address" to updatedUserInfo.address
//            // Add other fields if needed
//        )

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
}