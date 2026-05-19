package com.duongnd.pocketposapp.data.remote.dto.auth.login

import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginDTO(
    val tokens: Tokens,
    val user: UserDTO
)

@JsonClass(generateAdapter = true)
data class Tokens(
    val accessToken: String,
    val refreshToken: String
)

