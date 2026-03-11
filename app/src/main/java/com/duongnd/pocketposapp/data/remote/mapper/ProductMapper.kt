package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.data.remote.dto.ProductDTO
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product

// Mapper for Product
fun ProductDTO.toDomain(): Product {
    return Product(
        id = _id,
        name = name,
        barcode = barcode,
        category = categoryId.toDomain(),
        price = price,
        costPrice = costPrice,
        stock = stock,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Product.toDTO(): ProductDTO {
    return ProductDTO(
        _id = id,
        name = name,
        barcode = barcode,
        categoryId = category.toDTO(),
        price = price,
        costPrice = costPrice,
        stock = stock,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
