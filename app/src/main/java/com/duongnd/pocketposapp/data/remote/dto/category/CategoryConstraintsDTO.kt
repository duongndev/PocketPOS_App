package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryConstraintsDTO(
    val category: CategoryInfoDTO,
    val constraints: CategoryConstraintsInfoDTO,
    val warnings: CategoryWarningsDTO
)

@JsonClass(generateAdapter = true)
data class CategoryInfoDTO(
    val id: String,
    val name: String,
    val slug: String,
    val isActive: Boolean
)

@JsonClass(generateAdapter = true)
data class CategoryConstraintsInfoDTO(
    val childrenCount: Int,
    val productsCount: Int,
    val canDelete: Boolean,
    val hasActiveChildren: Boolean,
    val hasActiveProducts: Boolean
)

@JsonClass(generateAdapter = true)
data class CategoryWarningsDTO(
    val hasChildren: Boolean,
    val hasProducts: Boolean,
    val canSoftDelete: Boolean,
    val canHardDelete: Boolean
)
