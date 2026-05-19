package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<LoginDTO>
    suspend fun register(registerRequest: RegisterRequest): Result<UserDTO>
    suspend fun logout(): Result<Unit>
}
