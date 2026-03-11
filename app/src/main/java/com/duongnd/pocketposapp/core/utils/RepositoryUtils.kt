package com.duongnd.pocketposapp.core.utils

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> ApiResponse<T>,
    mapper: (T) -> R
): Result<R> {
    return try {
        val response = apiCall()
        if (response.success) {
            Result.success(mapper(response.data))
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun <T> safeApiCallRaw(
    apiCall: suspend () -> ApiResponse<T>
): Result<T> {
    return try {
        val response = apiCall()
        if (response.success) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
