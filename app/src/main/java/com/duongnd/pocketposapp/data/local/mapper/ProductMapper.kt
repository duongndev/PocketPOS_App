package com.duongnd.pocketposapp.data.local.mapper

import com.duongnd.pocketposapp.data.local.entity.ProductEntity
import com.duongnd.pocketposapp.data.local.entity.ProductVariantEntity
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant

fun ProductEntity.toDomain(variants: List<ProductVariant> = emptyList()): Product {
    return Product(
        id = id,
        categoryId = categoryId,
        name = name,
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
        id = id,
        categoryId = categoryId,
        name = name,
        description = description,
        imageUri = imageUri,
        hasVariants = hasVariants,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ProductVariantEntity.toDomain(): ProductVariant {
    return ProductVariant(
        id = variantId,
        productId = productId,
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = stock,
        isActive = isActive
    )
}

fun ProductVariant.toEntity(): ProductVariantEntity {
    return ProductVariantEntity(
        variantId = id,
        productId = productId,
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = stock,
        isActive = isActive
    )
}
