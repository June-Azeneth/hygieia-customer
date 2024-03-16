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
    val dateRegistered: Timestamp? = null,
    val status: String = ""
)