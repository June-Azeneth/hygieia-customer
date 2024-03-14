package com.example.hygieia_customer.model

import java.util.Date

class Promo(
    var id: String = "",
    var storeId: String = "",
    var product: String = "",
    var photo: String = "",
    var promoName: String = "",
    var discountedPrice: Double = 0.0,
    var discountRate: Double = 0.0,
    var pointsRequired: Double = 0.0,
    var dateStart: Date? = null,
    var dateEnd: Date? = null,
    var storeName: String = "",
) {
}