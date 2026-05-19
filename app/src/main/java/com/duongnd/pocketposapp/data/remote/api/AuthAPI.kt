package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ApiResponse<LoginDTO>

    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ApiResponse<UserDTO>

    @POST("/api/auth/logout")
    suspend fun logout(): ApiResponse<Unit>
}