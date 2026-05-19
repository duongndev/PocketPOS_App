package com.duongnd.pocketposapp.core.utils

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.squareup.moshi.Moshi
import retrofit2.HttpException

suspend fun <T, R> safeApiCall(
    moshi: Moshi,
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
    } catch (e: HttpException) {
        val errorResponse = parseErrorResponse(moshi, e)
        Result.failure(Exception(errorResponse?.message ?: "Đã xảy ra lỗi hệ thống"))
    } catch (e: Exception) {
        Result.failure(Exception("Lỗi kết nối: ${e.localizedMessage}"))
    }
}

suspend fun <T> safeApiCallRaw(
    moshi: Moshi,
    apiCall: suspend () -> ApiResponse<T>
): Result<T> {
    return try {
        val response = apiCall()
        if (response.success) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.message))
        }
    } catch (e: HttpException) {
        val errorResponse = parseErrorResponse(moshi, e)
        Result.failure(Exception(errorResponse?.message ?: "Đã xảy ra lỗi hệ thống"))
    } catch (e: Exception) {
        Result.failure(Exception("Lỗi kết nối: ${e.localizedMessage}"))
    }
}

private fun parseErrorResponse(moshi: Moshi, exception: HttpException): ApiResponse<*>? {
    return try {
        val errorBody = exception.response()?.errorBody()?.string()
        if (errorBody != null) {
            val adapter = moshi.adapter(ApiResponse::class.java)
            adapter.fromJson(errorBody)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}
