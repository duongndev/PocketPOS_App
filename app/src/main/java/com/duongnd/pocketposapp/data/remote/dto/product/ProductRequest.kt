package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant
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
    val name: String,
    val sku: String?,
    val barcode: String?,
    val price: Double,
    val stock: Int,
    val unit: String = "cái",
    val attributes: VariantAttributeRequest
)

@JsonClass(generateAdapter = true)
data class VariantAttributeRequest(
    val size: String? = null,
    val color: String? = null
)

fun Product.toRequest(): ProductRequest = ProductRequest(
    name = name,
    categoryId = categoryId,
    description = description ?: "",
    image = imageUri ?: "",
    hasVariants = hasVariants,
    variants = variants.map { it.toRequest(name) }
)

fun ProductVariant.toRequest(productName: String): ProductVariantRequest = ProductVariantRequest(
    name = if (attributes.isEmpty()) productName else "$productName (${attributes.joinToString(", ") { it.value }})",
    sku = sku,
    barcode = barcode,
    price = price,
    stock = stock,
    unit = unit,
    attributes = VariantAttributeRequest(
        size = attributes.find { it.attributeName.equals("Size", true) }?.value,
        color = attributes.find { it.attributeName.equals("Color", true) }?.value
    )
)
