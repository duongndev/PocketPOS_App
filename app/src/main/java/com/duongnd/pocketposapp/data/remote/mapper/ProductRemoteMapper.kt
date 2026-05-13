package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductMinimalDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductVariantDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductVariantWithProductDTO
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.domain.model.VariantAttribute
import java.util.Locale

fun ProductDTO.toDomainModel(): Product {
    return Product(
        id = id,
        categoryId = categoryId.id,
        categoryName = categoryId.name,
        name = name,
        brand = brand,
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
        name = name,
        productId = productId,
        sku = sku,
        barcode = barcode,
        price = price.toDouble(),
        costPrice = costPrice.toDouble(),
        stock = stock,
        unit = unit,
        isActive = true,
        attributes = domainAttributes
    )
}

fun ProductVariantWithProductDTO.toDomainModel(): ProductVariant {
    val domainAttributes = attributes?.map { (key, value) ->
        VariantAttribute(key.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, value)
    } ?: emptyList()

    return ProductVariant(
        id = id,
        name = name,
        productId = productId.id,
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = stock,
        unit = unit,
        isActive = isActive,
        attributes = domainAttributes
    )
}

fun ProductMinimalDTO.toDomainModel(): Product {
    return Product(
        id = id,
        categoryId = categoryId,
        categoryName = "",
        name = name,
        brand = brand,
        description = "",
        imageUri = image,
        hasVariants = true,
        variants = emptyList(),
        createdAt = "",
        updatedAt = ""
    )
}
