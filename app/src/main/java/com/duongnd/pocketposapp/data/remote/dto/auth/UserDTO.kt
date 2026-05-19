package com.duongnd.pocketposapp.data.remote.dto.auth

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    val id: String,
    val email: String,
    val username: String,
    val fullName: String,
    val phone: String,
    val role: String,
    val isActive: Boolean,
    val lastLoginAt: String?
)
