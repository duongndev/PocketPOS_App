package com.duongnd.pocketposapp.data.remote.dto.product

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductRequest(
    val name: String,
    val categoryId: String,
    val brand: String = "Generic",
    val description: String = "",
    val image: String = "",
    val isActive: Boolean = true,
    val hasVariants: Boolean = false,
    val variants: List<ProductVariantRequest>
)

@JsonClass(generateAdapter = true)
data class ProductVariantRequest(
    val sku: String?,
    val barcode: String?,
    val price: Double,
    val stock: Int,
    val attributes: VariantAttributeRequest
)

@JsonClass(generateAdapter = true)
data class VariantAttributeRequest(
    val size: String? = null,
    val color: String? = null
)
