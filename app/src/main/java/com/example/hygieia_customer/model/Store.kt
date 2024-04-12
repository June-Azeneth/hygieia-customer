package com.example.hygieia_customer.model

class Store(
    var storeId: String = "",
    val address: String = "",
    var email: String = "",
    val recyclables: List<*>? = null,
    var name: String = "",
    var photo: String = "",
    var googleMapLink: String = ""
)