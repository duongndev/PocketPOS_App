package com.duongnd.pocketposapp.data.local.mapper

import com.duongnd.pocketposapp.data.local.entity.CategoryEntity
import com.duongnd.pocketposapp.domain.model.Category

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id.toString(),
        name = name,
        slug = slug,
        parentId = parentId,
        sortOrder = sortOrder,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = if (id.isEmpty()) 0 else id.toIntOrNull() ?: 0,
        name = name,
        description = description,
        slug = slug,
        parentId = parentId,
        sortOrder = sortOrder,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
