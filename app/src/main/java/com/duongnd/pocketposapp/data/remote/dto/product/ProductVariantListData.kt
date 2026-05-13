package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.data.remote.dto.PaginationInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductVariantListData(
    val variants: List<ProductVariantWithProductDTO>,
    val pagination: PaginationInfo
)

@JsonClass(generateAdapter = true)
data class ProductVariantWithProductDTO(
    @field:Json(name = "_id")
    val id: String,
    val productId: ProductMinimalDTO,
    val name: String,
    val sku: String,
    val barcode: String,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val unit: String,
    val conversionValue: Int,
    val attributes: Map<String, String>?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@JsonClass(generateAdapter = true)
data class ProductMinimalDTO(
    @field:Json(name = "_id")
    val id: String,
    val name: String,
    val categoryId: String,
    val brand: String,
    val image: String
)
