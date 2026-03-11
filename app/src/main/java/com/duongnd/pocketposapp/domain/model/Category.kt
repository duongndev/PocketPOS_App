package com.duongnd.pocketposapp.domain.model

data class Category(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)
