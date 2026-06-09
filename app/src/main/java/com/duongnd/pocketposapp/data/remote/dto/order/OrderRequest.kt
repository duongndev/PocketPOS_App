package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderRequest(
    val paymentMethod: String,
    val note: String,
    val items: List<OrderItemRequest>
)
