package com.duongnd.pocketposapp.data.local.mapper

import com.duongnd.pocketposapp.data.local.entity.ProductEntity
import com.duongnd.pocketposapp.data.local.entity.ProductVariantEntity
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant

fun ProductEntity.toDomain(variants: List<ProductVariant> = emptyList()): Product {
    return Product(
        id = id.toString(),
        categoryId = categoryId.toString(),
        categoryName = categoryName,
        name = name,
        brand = brand,
        description = description,
        imageUri = imageUri,
        hasVariants = hasVariants,
        variants = variants,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id.toIntOrNull() ?: 0,
        categoryId = categoryId.toIntOrNull() ?: 0,
        categoryName = categoryName,
        name = name,
        brand = brand,
        description = description,
        imageUri = imageUri,
        hasVariants = hasVariants,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ProductVariantEntity.toDomain(): ProductVariant {
    return ProductVariant(
        id = variantId.toString(),
        name = name,
        productId = productId.toString(),
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = stock,
        unit = unit,
        isActive = isActive
    )
}

fun ProductVariant.toEntity(): ProductVariantEntity {
    return ProductVariantEntity(
        variantId = id.toIntOrNull() ?: 0,
        name = name,
        productId = productId.toIntOrNull() ?: 0,
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = stock,
        unit = unit,
        isActive = isActive
    )
}
