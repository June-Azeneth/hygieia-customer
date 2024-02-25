package com.example.hygieia_customer.model

import com.google.firebase.Timestamp

class Transaction(
    val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val discount: Double = 0.0,
    val pointsEarned: Double = 0.0,
    val pointsSpent: Double = 0.0,
    val product: String = "",
    val storeName: String = "",
    val total: Double = 0.0,
    val addedOn: Timestamp? = null,
    val type: String = "",
)