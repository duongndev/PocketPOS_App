package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentStatusRequest(
    val paymentStatus: String = "paid"
)
