package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.data.remote.dto.category.CategoryDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDTO(
    @field:Json(name = "_id")
    val id: String,
    val name: String,
    val slug: String,
    val categoryId: CategoryDTO,
    val brand: String,
    val description: String,
    val images: String,
    val hasVariants: Boolean,
    val options: List<ProductOptionDTO>,
    val tags: List<String>,
    val isActive: Boolean,
    val deletedAt: String?,
    val variants: List<ProductVariantDTO>,
    val createdAt: String,
    val updatedAt: String,
)

@JsonClass(generateAdapter = true)
data class ProductOptionDTO(
    val name: String,
    val values: List<String>
)
