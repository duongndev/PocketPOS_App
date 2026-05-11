package com.duongnd.pocketposapp.domain.model

data class CategoryPage(
    val categories: List<Category>,
    val pagination: PaginationInfo
)

data class PaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val nextPage: Int? = null,
    val prevPage: Int? = null
)
