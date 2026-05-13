package com.duongnd.pocketposapp.data.remote.dto.category

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategorySoftDeleteData(
    val id: String,
    val name: String,
    val deleted: Boolean,
    val affectedProducts: Int
)

@JsonClass(generateAdapter = true)
data class CategoryHardDeleteData(
    val id: String,
    val name: String,
    val permanentlyDeleted: Boolean
)
