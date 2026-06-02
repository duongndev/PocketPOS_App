package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.duongnd.pocketposapp.domain.model.Product

fun ProductDTO.toDomainModel(): Product {
    val categoryIdStr = when (val cat = categoryId) {
        is String -> cat
        is Map<*, *> -> cat["_id"] as? String ?: ""
        else -> ""
    }
    
    val categoryNameStr = when (val cat = categoryId) {
        is Map<*, *> -> cat["name"] as? String ?: ""
        else -> ""
    }

    return Product(
        id = id,
        storeId = storeId,
        categoryId = categoryIdStr,
        categoryName = categoryNameStr,
        name = name,
        sku = sku,
        barcode = barcode,
        brand = brand,
        imageUrl = imageUrl,
        costPrice = costPrice,
        sellingPrice = sellingPrice,
        stock = stock,
        unit = unit,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
