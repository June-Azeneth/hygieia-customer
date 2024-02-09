package com.example.hygieia_customer.model

class Reward(
    var id: String = "",
    var product: String = "",
//    var store_price : Double = 0.0,
    var pointsRequired: Double = 0.0,
    var photo: String = "",
    var discount: Double = 0.0,
    var discountedPrice: Double = 0.0,
    var storeName: String = ""
)
{
    override fun toString(): String {
        return "Reward(id=$product, /* Other properties */)"
    }
}