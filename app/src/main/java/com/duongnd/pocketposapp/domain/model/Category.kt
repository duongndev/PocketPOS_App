package com.duongnd.pocketposapp.domain.model

data class Category(
    val id: String = "",
    val name: String,
    val slug: String,
    val description: String? = null,
    val parentId: String? = null,
    val sortOrder: Int? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)
