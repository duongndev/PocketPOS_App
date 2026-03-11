package com.duongnd.pocketposapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductListData(
    val products: List<ProductDTO>,
    val pagination: PaginationInfo
)

@JsonClass(generateAdapter = true)
data class PaginationInfo(
    val total: Int,
    val page: Int,
    val limit: Int,
    val pages: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean
)
