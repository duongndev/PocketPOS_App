package com.duongnd.pocketposapp.data.remote.dto.auth.register

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val fullName: String,
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val phone: String,
)