package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.domain.model.VariantAttribute
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductVariantDTO(
    @field:Json(name = "_id")
    val id: String,
    val productId: String,
    val name: String,
    val sku: String,
    val barcode: String,
    val price: Int,
    val stock: Int,
    val unit: String,
    val attributes: VariantAttributeDTO
)
