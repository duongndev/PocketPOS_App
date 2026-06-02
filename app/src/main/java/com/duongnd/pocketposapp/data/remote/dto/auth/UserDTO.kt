package com.duongnd.pocketposapp.data.remote.dto.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    @field:Json(name = "_id")
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val avatar: String?,
    val role: String,
    @field:Json(name = "storeId")
    val store: UserStoreDTO?,
    val lastLoginAt: String?,
    val isActive: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class UserStoreDTO(
    @field:Json(name = "_id")
    val id: String,
    val storeName: String,
    val logoUrl: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val ownerId: String
)
