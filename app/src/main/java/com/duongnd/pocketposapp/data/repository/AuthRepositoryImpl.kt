package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.core.utils.safeActionCallRaw
import com.duongnd.pocketposapp.data.remote.api.AuthAPI
import com.duongnd.pocketposapp.data.remote.dto.auth.UserDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginDTO
import com.duongnd.pocketposapp.data.remote.dto.auth.login.LoginRequest
import com.duongnd.pocketposapp.data.remote.dto.auth.register.RegisterRequest
import com.duongnd.pocketposapp.domain.repository.AuthRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authAPI: AuthAPI,
    private val sharedPrefs: ShareReferenceManager,
    private val moshi: Moshi
) : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<LoginDTO> {
        return safeActionCallRaw(moshi) { authAPI.login(loginRequest) }.onSuccess { response ->
            sharedPrefs.saveTokens(response.tokens.accessToken, response.tokens.refreshToken)
            sharedPrefs.saveUser(response.user)
            response.user.store?.let { sharedPrefs.saveStore(it) }
        }
    }

    override suspend fun register(registerRequest: RegisterRequest): Result<UserDTO> {
        return safeActionCallRaw(moshi) { authAPI.register(registerRequest) }
    }

    override suspend fun logout(): Result<Unit> {
        return safeActionCallRaw(moshi) { authAPI.logout() }.onSuccess {
            sharedPrefs.clearAll()
        }
    }

    override suspend fun getMe(): Result<UserDTO> {
        return safeActionCallRaw(moshi) { authAPI.getMe() }.onSuccess { user ->
            sharedPrefs.saveUser(user)
            user.store?.let { sharedPrefs.saveStore(it) }
        }
    }
}
