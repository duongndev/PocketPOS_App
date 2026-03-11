package com.duongnd.pocketposapp.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDTO(
    val _id: String,
    val name: String,
    val description: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
