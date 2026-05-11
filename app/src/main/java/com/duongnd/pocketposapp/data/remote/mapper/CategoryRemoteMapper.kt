package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.category.*
import com.duongnd.pocketposapp.domain.model.*

fun CategoryDTO.toDomainModel(): Category {
    return Category(
        id = id,
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

fun CategoryTreeDTO.toDomainModel(): CategoryTree {
    return CategoryTree(
        id = id,
        name = name,
        slug = slug,
        description = description,
        sortOrder = sortOrder,
        isActive = isActive,
        children = children.map { it.toDomainModel() }
    )
}

fun CategoryConstraintsDTO.toDomainModel(): CategoryConstraints {
    return CategoryConstraints(
        category = CategoryMinimal(
            id = category.id,
            name = category.name,
            slug = category.slug,
            isActive = category.isActive
        ),
        constraints = ConstraintsInfo(
            childrenCount = constraints.childrenCount,
            productsCount = constraints.productsCount,
            canDelete = constraints.canDelete,
            hasActiveChildren = constraints.hasActiveChildren,
            hasActiveProducts = constraints.hasActiveProducts
        ),
        warnings = CategoryWarnings(
            hasChildren = warnings.hasChildren,
            hasProducts = warnings.hasProducts,
            canSoftDelete = warnings.canSoftDelete,
            canHardDelete = warnings.canHardDelete
        )
    )
}

fun CategoryPaginationInfo.toDomainModel(): PaginationInfo {
    return PaginationInfo(
        currentPage = currentPage,
        totalPages = totalPages,
        totalItems = totalItems,
        itemsPerPage = itemsPerPage,
        hasNextPage = hasNextPage,
        hasPrevPage = hasPrevPage,
        nextPage = nextPage,
        prevPage = prevPage
    )
}

fun CategoryListData.toDomainPage(): CategoryPage {
    return CategoryPage(
        categories = categories.map { it.toDomainModel() },
        pagination = pagination.toDomainModel()
    )
}
