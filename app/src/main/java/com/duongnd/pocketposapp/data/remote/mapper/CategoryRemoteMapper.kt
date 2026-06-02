package com.duongnd.pocketposapp.data.remote.mapper

import com.duongnd.pocketposapp.data.remote.dto.category.*
import com.duongnd.pocketposapp.domain.model.*

fun CategoryDTO.toDomainModel(): Category {
    return Category(
        id = id,
        storeId = storeId,
        name = name,
        description = description,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
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
        prevPage = prevPage,
        isFirstPage = isFirstPage,
        isLastPage = isLastPage
    )
}

fun List<CategoryDTO>.toDomainList(): List<Category> {
    return this.map { it.toDomainModel() }
}
