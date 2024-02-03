package com.example.hygieia_customer.model

class Transaction(
    val customerID: String = "",
    val customerName: String = "",
    val date: String = "",
    val discount: Double = 0.0,
    val points_earned: Double = 0.0,
    val points_spent: Double = 0.0,
    val product: String = "",
    val storeName: String = "",
    val total: Double = 0.0,
    val type: String = "",
    ) {
}