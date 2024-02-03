package com.example.hygieia_customer.model

class UserInfo(
    val userPhoto : String = "",
    val userLocation : String = "",
    val email : String = "",
    val userID : String = "",
    val customerName: String = "",
    val currentBalance: Double = 0.0,
    val img_url: String? = "",
)

fun UserInfo.isDifferent(other: UserInfo): Boolean {
    return this.customerName != other.customerName ||
            this.email != other.email ||
            this.userLocation != other.userLocation ||
            this.img_url != other.img_url ||
            this.userPhoto != other.userPhoto ||
            this.userID != other.userID ||
            this.currentBalance != other.currentBalance
    // Add other fields for comparison if needed
}