package com.duongnd.pocketposapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginationDTO(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val hasPrevPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val itemsPerPage: Int,
    val nextPage: Int,
    val prevPage: Any,
    val totalItems: Int,
    val totalPages: Int
)