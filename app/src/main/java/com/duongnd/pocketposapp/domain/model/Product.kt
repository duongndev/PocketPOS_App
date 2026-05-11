package com.duongnd.pocketposapp.domain.model

data class Product(
    val id: String = "",
    val categoryId: String,
    val name: String,
    val description: String? = null,
    val imageUri: String? = null,
    val hasVariants: Boolean = false,
    val variants: List<ProductVariant> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ProductVariant(
    val id: String = "",
    val productId: String,
    val sku: String? = null,
    val barcode: String? = null,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val isActive: Boolean = true,
    val attributes: List<VariantAttribute> = emptyList()
)

data class VariantAttribute(
    val attributeName: String, // Ví dụ: Màu sắc
    val value: String         // Ví dụ: Xanh
)
