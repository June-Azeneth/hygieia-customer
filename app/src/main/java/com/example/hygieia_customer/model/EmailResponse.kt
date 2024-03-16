package com.example.hygieia_customer.model

data class EmailResponse(
    val email: String = "",
    val didYouMean: String = "",
    val user: String = "",
    val domain: String = "",
    val formatValid: Boolean = false,
    val mxFound: Boolean = false,
    val smtpCheck: Boolean = false,
    val catchAll: Boolean = false,
    val role: Boolean = false,
    val disposable: Boolean = false,
    val free: Boolean = false,
    val score: Double = 0.0
)