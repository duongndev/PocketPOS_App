package com.duongnd.pocketposapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDTO(
    val _id: String,
    val name: String,
    val barcode: String,
    val categoryId: CategoryDTO,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
)
