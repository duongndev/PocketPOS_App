package com.duongnd.pocketposapp.data.remote.dto

import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductListData(
    val products: List<ProductDTO>,
    val pagination: PaginationInfo
)

@JsonClass(generateAdapter = true)
data class PaginationInfo(
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
