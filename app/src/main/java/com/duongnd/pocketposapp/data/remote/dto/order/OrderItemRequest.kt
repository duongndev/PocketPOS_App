package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
)
