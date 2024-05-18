package com.example.hygieia_customer.model

class Transaction(
    var id: String = "",
    var customerName: String = "",
    var customerId: String = "",
    var type: String = "",
    var rewardId: String = "",
    var addedOn: java.util.Date? = null,
    var pointsSpent: Double = 0.0,
    var pointsEarned: Double = 0.0,
    var total: Double = 0.0,
    var product: String = "",
    var promoName: String = "",
    var storeId: String = "",
    var storeName: String = "",
    var discount: Double = 0.0,
    var promoId: String = "",
    var totalPointsSpent : Double = 0.0,
)