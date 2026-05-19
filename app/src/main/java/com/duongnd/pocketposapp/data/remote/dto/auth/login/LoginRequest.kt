package com.duongnd.pocketposapp.data.remote.dto.auth.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String
)