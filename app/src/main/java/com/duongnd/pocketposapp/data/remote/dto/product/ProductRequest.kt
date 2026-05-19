package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductOption
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.domain.model.VariantAttribute
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
    val options: List<ProductOptionRequest> = emptyList(),
    val tags: List<String> = emptyList(),
    val variants: List<ProductVariantRequest>
)

@JsonClass(generateAdapter = true)
data class ProductOptionRequest(
    val name: String,
    val values: List<String>
)

@JsonClass(generateAdapter = true)
data class ProductVariantRequest(
    val sku: String?,
    val barcode: String?,
    val price: Double,
    val costPrice: Double,
    val inventory: InventoryRequest,
    val unit: String = "cái",
    val conversionRate: Double = 1.0,
    val attributes: List<VariantAttributeRequest>,
    val image: String = "",
    val isDefault: Boolean = false,
    val lowStockThreshold: Int = 5,
    val isActive: Boolean = true
)

@JsonClass(generateAdapter = true)
data class InventoryRequest(
    val quantity: Int,
    val reserved: Int = 0
)

@JsonClass(generateAdapter = true)
data class VariantAttributeRequest(
    val name: String,
    val value: String
)

fun Product.toRequest(): ProductRequest = ProductRequest(
    name = name,
    categoryId = categoryId,
    brand = brand,
    description = description ?: "",
    image = imageUri ?: "",
    isActive = isActive,
    hasVariants = hasVariants,
    options = options.map { it.toRequest() },
    tags = tags,
    variants = variants.map { it.toRequest() }
)

fun ProductOption.toRequest(): ProductOptionRequest = ProductOptionRequest(
    name = name,
    values = values
)

fun ProductVariant.toRequest(): ProductVariantRequest = ProductVariantRequest(
    sku = sku,
    barcode = barcode,
    price = price,
    costPrice = costPrice,
    inventory = InventoryRequest(quantity = stock, reserved = reserved),
    unit = unit,
    conversionRate = conversionRate,
    attributes = attributes.map { it.toRequest() },
    image = imageUri ?: "",
    isDefault = isDefault,
    lowStockThreshold = lowStockThreshold,
    isActive = isActive
)

fun VariantAttribute.toRequest(): VariantAttributeRequest = VariantAttributeRequest(
    name = name,
    value = value
)
