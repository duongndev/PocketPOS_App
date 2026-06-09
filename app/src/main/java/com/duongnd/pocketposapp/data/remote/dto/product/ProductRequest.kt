package com.duongnd.pocketposapp.data.remote.dto.product

import com.duongnd.pocketposapp.domain.model.Product
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductRequest(
    val categoryId: String,
    val name: String,
    val barcode: String?,
    val brand: String?,
    val imageUrl: String?,
    val costPrice: Double,
    val sellingPrice: Double,
    val stock: Int,
    val unit: String?,
    val description: String?
)

fun Product.toRequest(): ProductRequest = ProductRequest(
    categoryId = categoryId,
    name = name,
    barcode = barcode,
    brand = brand,
    imageUrl = imageUrl,
    costPrice = costPrice,
    sellingPrice = sellingPrice,
    stock = stock,
    unit = unit,
    description = description
)
