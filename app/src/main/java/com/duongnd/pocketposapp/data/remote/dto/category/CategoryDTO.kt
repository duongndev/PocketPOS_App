package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDTO(
    @field:Json(name = "_id")
    val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val parentId: String?,
    val sortOrder: Int?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)