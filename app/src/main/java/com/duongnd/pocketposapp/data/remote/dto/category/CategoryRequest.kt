package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryRequest(
    val name: String,
    val description: String,
    val parentId: String?,
    val sortOrder: Int?
)
