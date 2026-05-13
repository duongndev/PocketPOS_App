package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.data.remote.dto.category.CategoryDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDTO(
    @field:Json(name = "_id")
    val id: String,
    val name: String,
    val categoryId: CategoryDTO,
    val brand: String,
    val description: String,
    val image: String,
    val isActive: Boolean,
    val variants: List<ProductVariantDTO>,
    val createdAt: String,
    val updatedAt: String,
)