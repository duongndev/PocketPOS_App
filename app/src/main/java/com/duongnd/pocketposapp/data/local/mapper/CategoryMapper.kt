package com.duongnd.pocketposapp.data.local.mapper

import com.duongnd.pocketposapp.data.local.entity.CategoryEntity
import com.duongnd.pocketposapp.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
