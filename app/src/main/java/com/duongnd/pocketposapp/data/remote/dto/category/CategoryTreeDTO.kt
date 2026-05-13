package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryTreeDTO(
    @field:Json(name = "_id")
    val id: String,
    val name: String,
    val slug: String,
    val description: String?,
    val sortOrder: Int?,
    val isActive: Boolean,
    val children: List<CategoryTreeDTO> = emptyList()
)
