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

    fun checkAccountStatus(callback: (String) -> Unit) {
        try {
            val userRef = fireStore.collection("consumer")
                .whereEqualTo("id", getCurrentUserId())

            userRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents.firstOrNull()
                        val status = document?.getString("status") ?: ""
                        callback(status)
                    }
                }
                .addOnFailureListener {
                    callback("")
                }
        } catch (error: Exception) {
            callback("")
        }
    }

    fun getUserDetails(userId: String, callback: (UserInfo?) -> Unit) {
        val userRef = fireStore.collection("consumer").whereEqualTo("id", userId)
        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                try {
                    if (!documentSnapshot.isEmpty) {
                        val document = documentSnapshot.documents[0]
                        val user = document.data // Retrieve all data from the document
                        val userData = UserInfo(
                            document.id,
                            document.getString("firstName") ?: "",
                            document.getString("lastName") ?: "",
                            document.getString("photo") ?: "",
                            user?.get("address") as? Map<String, String>?,
                            document.getString("email") ?: "",
                            document.getDouble("currentBalance") ?: 0.0,
                            document.getString("qrCode") ?: "",
                        )
                        callback(userData)
                    } else {
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

//    fun getEmail(): String? {
//        val currentUser = auth.currentUser
//        return currentUser?.email
//    }

    fun updateUserProfile(userId: String, updatedUserInfo: UserInfo, callback: (Boolean) -> Unit) {
        val userRef = fireStore.collection("consumer").document(userId)
        val updateData = mapOf(
            "photo" to updatedUserInfo.photo,
            "firstName" to updatedUserInfo.firstName,
            "lastName" to updatedUserInfo.lastName,
            "address" to updatedUserInfo.address
        )
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
                            document.getString("address") ?: "",
                            document.getString("email") ?: "",
                            storeData?.get("recyclable") as? List<*>?,
                            document.getString("name") ?: "",
                            document.getString("photo") ?: "",
                            document.getString("googleMapLocation") ?: ""
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

//    fun customerExist(callback: (Boolean) -> Unit) {
//        val userRef = fireStore.collection("consumer")
//            .whereEqualTo("id", getCurrentUserId())
//
//        userRef.get()
//            .addOnSuccessListener { documents ->
//                if (documents != null && !documents.isEmpty) {
//                    callback(true)
//                } else {
//                    callback(false)
//                }
//            }
//            .addOnFailureListener { exception ->
//                callback(false)
//                Commons().log(logTag, exception.message.toString())
//            }
//    }

    fun activateAccount(callback: (Boolean) -> Unit) {
        fireStore.collection("consumer")
            .whereEqualTo("id", getCurrentUserId())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false)
                } else {
                    val document = documents.first()
                    val status = document.getString("status")

                    if (status == "unauthenticated") {
                        document.reference.update("status", "active")
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnCanceledListener {
                                callback(false)
                            }
                    } else {
                        callback(true)
                    }
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}