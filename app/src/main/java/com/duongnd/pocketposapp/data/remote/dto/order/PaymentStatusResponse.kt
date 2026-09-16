package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentStatusResponse(
    val orderId: String,
    val orderNumber: String,
    val paymentStatus: String,
    val orderStatus: String
)
