package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.CategoryDTO
import com.duongnd.pocketposapp.data.remote.dto.CategoryListData
import retrofit2.http.*

interface CategoryAPI {
    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<CategoryListData>

    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: String
    ): ApiResponse<CategoryDTO>

    @POST("categories")
    suspend fun createCategory(
        @Body category: CategoryDTO
    ): ApiResponse<CategoryDTO>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: CategoryDTO
    ): ApiResponse<CategoryDTO>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): ApiResponse<Unit>
}
