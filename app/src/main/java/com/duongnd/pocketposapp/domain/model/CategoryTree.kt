package com.duongnd.pocketposapp.domain.model

data class CategoryTree(
    val id: String,
    val name: String,
    val slug: String,
    val description: String?,
    val sortOrder: Int?,
    val isActive: Boolean,
    val children: List<CategoryTree>
)
