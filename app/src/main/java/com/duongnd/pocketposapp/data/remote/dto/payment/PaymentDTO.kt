package com.duongnd.pocketposapp.data.remote.dto.payment

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentDTO(
    @field:Json(name = "_id")
    val id: String,
    val orderId: String,
    val storeId: String,
    val amount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val qrContent: String?,
    val paidAt: String?,
    val createdAt: String,
    val updatedAt: String
)
