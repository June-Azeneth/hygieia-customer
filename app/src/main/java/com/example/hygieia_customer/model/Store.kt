package com.example.hygieia_customer.model

class Store(
    var storeId: String = "",
    val address: Map<*, *>? = null,
    var email: String = "",
    val recyclables: List<*>? = null,
    var name: String = "",
    var photo: String = ""
)