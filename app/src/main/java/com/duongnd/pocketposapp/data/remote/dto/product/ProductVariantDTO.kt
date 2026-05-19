package com.duongnd.pocketposapp.data.remote.dto.product

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductVariantDTO(
    @field:Json(name = "_id")
    val id: String,
    val productId: String,
    val sku: String,
    val barcode: String,
    val price: Double,
    val costPrice: Double,
    val inventory: InventoryDTO,
    val unit: String,
    val conversionRate: Double,
    val attributes: List<VariantAttributeDTO>,
    val images: String,
    val isDefault: Boolean,
    val lowStockThreshold: Int,
    val isActive: Boolean
)

@JsonClass(generateAdapter = true)
data class InventoryDTO(
    val quantity: Int,
    val reserved: Int
)
