package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ActionResponse
import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthAPI {
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ActionResponse<LoginDTO>

    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ActionResponse<UserDTO>

    @POST("/api/auth/logout")
    suspend fun logout(): ActionResponse<Unit>

    @GET("/api/auth/me")
    suspend fun getMe(): ActionResponse<UserDTO>
}
