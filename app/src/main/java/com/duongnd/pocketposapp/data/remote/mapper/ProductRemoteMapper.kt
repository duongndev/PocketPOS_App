package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductVariantDTO
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.domain.model.VariantAttribute

fun ProductDTO.toDomainModel(): Product {
    return Product(
        id = id,
        categoryId = categoryId.id,
        name = name,
        description = description,
        imageUri = image,
        hasVariants = variants.isNotEmpty(),
        variants = variants.map { it.toDomainModel() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ProductVariantDTO.toDomainModel(): ProductVariant {
    val domainAttributes = mutableListOf<VariantAttribute>()
    if (!attributes.size.isNullOrEmpty()) {
        domainAttributes.add(VariantAttribute("Size", attributes.size))
    }
    if (!attributes.color.isNullOrEmpty()) {
        domainAttributes.add(VariantAttribute("Color", attributes.color))
    }
    
    return ProductVariant(
        id = id,
        productId = productId,
        sku = sku,
        barcode = barcode,
        price = price.toDouble(),
        costPrice = 0.0,
        stock = stock,
        isActive = true,
        attributes = domainAttributes
    )
}
