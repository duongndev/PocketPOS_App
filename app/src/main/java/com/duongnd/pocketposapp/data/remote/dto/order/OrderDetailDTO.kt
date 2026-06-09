package com.duongnd.pocketposapp.data.remote.dto.order

import com.duongnd.pocketposapp.data.remote.dto.payment.PaymentDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDetailDTO(
    val order: OrderDTO,
    val items: List<OrderItemDTO>,
    val payment: PaymentDTO
)
