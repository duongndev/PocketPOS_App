package com.duongnd.pocketposapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryListData(
    val categories: List<CategoryDTO>,
    val pagination: CategoryPaginationInfo
)

@JsonClass(generateAdapter = true)
data class CategoryPaginationInfo(
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val itemsPerPage: Int,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val nextPage: Int?,
    val prevPage: Int?,
    val isFirstPage: Boolean,
    val isLastPage: Boolean
)
