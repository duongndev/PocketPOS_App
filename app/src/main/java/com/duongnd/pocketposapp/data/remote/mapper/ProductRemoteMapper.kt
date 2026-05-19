package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductOptionDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductVariantDTO
import com.duongnd.pocketposapp.data.remote.dto.product.VariantAttributeDTO
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductOption
import com.duongnd.pocketposapp.domain.model.ProductVariant
import com.duongnd.pocketposapp.domain.model.VariantAttribute

fun ProductDTO.toDomainModel(): Product {
    return Product(
        id = id,
        name = name,
        slug = slug,
        categoryId = categoryId.id,
        categoryName = categoryId.name,
        brand = brand,
        description = description,
        imageUri = images,
        hasVariants = hasVariants,
        options = options.map { it.toDomainModel() },
        tags = tags,
        isActive = isActive,
        deletedAt = deletedAt,
        variants = variants.map { it.toDomainModel(this.name) },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun ProductOptionDTO.toDomainModel(): ProductOption {
    return ProductOption(
        name = name,
        values = values
    )
}

fun ProductVariantDTO.toDomainModel(productName: String = ""): ProductVariant {
    val domainAttributes = attributes.map { it.toDomainModel() }
    val variantName = if (domainAttributes.isNotEmpty()) {
        "$productName (${domainAttributes.joinToString(", ") { it.value }})"
    } else {
        productName
    }

    return ProductVariant(
        id = id,
        name = variantName,
        productId = productId,
        sku = sku,
        barcode = barcode,
        price = price,
        costPrice = costPrice,
        stock = inventory.quantity,
        reserved = inventory.reserved,
        unit = unit,
        conversionRate = conversionRate,
        attributes = domainAttributes,
        imageUri = images,
        isDefault = isDefault,
        lowStockThreshold = lowStockThreshold,
        isActive = isActive
    )
}

fun VariantAttributeDTO.toDomainModel(): VariantAttribute {
    return VariantAttribute(
        name = name,
        value = value
    )
}
