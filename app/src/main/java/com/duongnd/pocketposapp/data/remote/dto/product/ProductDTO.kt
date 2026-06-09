package com.duongnd.pocketposapp.data.remote.dto.product

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDTO(
    @field:Json(name = "_id")
    val id: String,
    val storeId: String,
    val categoryId: Any?, // Can be String (ID) or Object (populated)
    val name: String,
    val sku: String?,
    val barcode: String?,
    val brand: String?,
    val imageUrl: String?,
    val costPrice: Double,
    val sellingPrice: Double,
    val stock: Int,
    val unit: String?,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
