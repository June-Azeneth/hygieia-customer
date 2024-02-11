package com.example.hygieia_customer.model

import com.google.firebase.Timestamp

class UserInfo(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val photo: String = "",
    val address: Map<String, String>? = null,
    val email: String = "",
    val currentBalance: Double = 0.0,
    val qrCode: String = "",
    val dateRegistered : Timestamp? = null,

    )
//
//fun UserInfo.isDifferent(other: UserInfo): Boolean {
//    return this.customerName != other.customerName ||
//            this.email != other.email ||
//            this.userLocation != other.userLocation ||
//            this.img_url != other.img_url ||
//            this.userPhoto != other.userPhoto ||
//            this.userID != other.userID ||
//            this.currentBalance != other.currentBalance
//    // Add other fields for comparison if needed
//}