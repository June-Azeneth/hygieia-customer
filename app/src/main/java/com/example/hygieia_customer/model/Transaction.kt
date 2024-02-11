package com.example.hygieia_customer.model

import java.util.Date
import com.google.firebase.Timestamp

class Transaction(
    val id: String = "",
    val customerID: String = "",
    val customerName: String = "",
    val discount: Double = 0.0,
    val points_earned: Double = 0.0,
    val points_spent: Double = 0.0,
    val product: String = "",
    val storeName: String = "",
    val total: Double = 0.0,
    val addedOn: Timestamp? = null,
    val type: String = "",
)