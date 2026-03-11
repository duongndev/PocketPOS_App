package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.domain.model.Category

// Mapper for Category
fun CategoryDTO.toDomain(): Category {
    return Category(
        id = _id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Category.toDTO(): CategoryDTO {
    return CategoryDTO(
        _id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
