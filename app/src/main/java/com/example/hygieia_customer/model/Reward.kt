package com.example.hygieia_customer.model

import com.google.firebase.Timestamp

class Reward(
    var id: String = "",
    var name: String = "",
    var pointsRequired: Double = 0.0,
    var photo: String = "",
    var discount: Double = 0.0,
    var discountedPrice: Double = 0.0,
    var storeName: String = "",
    var storeId: String = "",
    var price: Double = 0.0,
    var description: String = "",
    var addedOn: Timestamp? = null,
)