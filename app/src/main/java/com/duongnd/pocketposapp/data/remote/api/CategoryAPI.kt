package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ActionResponse
import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.category.*
import retrofit2.http.*

interface CategoryAPI {
    @GET("/api/categories")
    suspend fun getCategories(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null
    ): ApiResponse<List<CategoryDTO>>

    @GET("/api/categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: String
    ): ApiResponse<CategoryDTO>

    @POST("/api/categories")
    suspend fun createCategory(
        @Body categoryRequest: CategoryRequest
    ): ActionResponse<CategoryDTO>

    @PUT("/api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body categoryRequest: CategoryRequest
    ): ActionResponse<CategoryDTO>

    @DELETE("/api/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): ActionResponse<Unit>
}
