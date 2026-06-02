package com.duongnd.pocketposapp.data.remote.dto

import com.duongnd.pocketposapp.data.remote.dto.category.CategoryPaginationInfo
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T,
    val pagination: CategoryPaginationInfo? = null
)

@JsonClass(generateAdapter = true)
data class ActionResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
