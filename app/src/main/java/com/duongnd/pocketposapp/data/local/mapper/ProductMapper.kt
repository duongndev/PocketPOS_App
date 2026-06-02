package com.duongnd.pocketposapp.data.local.mapper

import com.duongnd.pocketposapp.data.local.entity.ProductEntity
import com.duongnd.pocketposapp.domain.model.Product

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        storeId = storeId,
        categoryId = categoryId,
        categoryName = categoryName,
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

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        storeId = storeId,
        categoryId = categoryId,
        categoryName = categoryName,
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
