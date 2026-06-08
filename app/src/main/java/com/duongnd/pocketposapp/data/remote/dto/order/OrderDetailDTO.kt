package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDetailDTO(
    val order: OrderDTO,
    val items: List<OrderItemDTO>
)
