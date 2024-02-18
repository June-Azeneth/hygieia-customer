package com.example.hygieia_customer.model

import java.util.Date

class Promo(
    var storeId: String,
    var storeName: String,
    var id: String,
    var product: String,
    var photo: String,
    var promoName: String,
    var discountedPrice: Double,
    var discountRate: Double,
    var pointsRequired: Double,
    var dateStart: Date?,
    var dateEnd: Date?,
) {
}