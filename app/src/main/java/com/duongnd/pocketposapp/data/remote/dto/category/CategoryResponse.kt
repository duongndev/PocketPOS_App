package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryPaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val nextPage: Int? = null,
    val prevPage: Int? = null,
    val isFirstPage: Boolean,
    val isLastPage: Boolean
)
