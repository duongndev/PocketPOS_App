package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderCreateResponse(
    val orderId: String,
    val orderNumber: String,
    val totalAmount: Int,
    val totalCost: Int,
    val profit: Int,
    val paymentMethod: String,
    val paymentStatus: String,
    val qrUrl: String?,
)
