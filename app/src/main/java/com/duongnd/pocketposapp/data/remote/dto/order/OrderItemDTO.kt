package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderItemDTO(
    val barcode: String,
    val costPrice: Int,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val sellingPrice: Int,
    val sku: String,
    val subtotal: Int
)