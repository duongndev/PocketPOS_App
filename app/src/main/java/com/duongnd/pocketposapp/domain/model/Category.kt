package com.duongnd.pocketposapp.domain.model

data class Category(
    val id: String,
    val storeId: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
