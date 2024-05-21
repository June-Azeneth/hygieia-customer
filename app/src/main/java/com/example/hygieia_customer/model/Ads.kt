package com.example.hygieia_customer.model

import java.util.Date

class Ads(
    var id: String = "",
    var startDate: Date? = null,
    var endDate: Date? = null,
    var poster: String = "",
    var storeId: String = "",
    var status: String = "",
    var title: String = "",
    var details : String = ""
)