package com.duongnd.pocketposapp.feature.auth

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
