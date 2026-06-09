package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderDTO (
    @field:Json(name = "_id")
    val id: String,
    val storeId: String,
    val orderNumber: String,
    val items: List<OrderItemDTO>? = null,
    val totalCost: Int,
    val totalAmount: Int,
    val totalQuantity: Int,
    val profit: Int,
    val status: String,
    val note: String,
    val createdBy: OrderCreatedByDTO,
    val createdAt: String,
    val updatedAt: String
)