package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.category.*
import retrofit2.http.*

interface CategoryAPI {
    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = null,
        @Query("isActive") isActive: Boolean? = null,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null
    ): ApiResponse<CategoryListData>

    @GET("categories/tree")
    suspend fun getCategoryTree(): ApiResponse<List<CategoryTreeDTO>>

    @GET("categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: String
    ): ApiResponse<CategoryDTO>

    @GET("categories/{id}/constraints")
    suspend fun getCategoryConstraints(
        @Path("id") id: String
    ): ApiResponse<CategoryConstraintsDTO>

    @POST("categories")
    suspend fun createCategory(
        @Body categoryRequest: CategoryRequest
    ): ApiResponse<CategoryDTO>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body categoryRequest: CategoryRequest
    ): ApiResponse<CategoryDTO>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): ApiResponse<CategorySoftDeleteData>

    @DELETE("categories/{id}/hard")
    suspend fun hardDeleteCategory(
        @Path("id") id: String
    ): ApiResponse<CategoryHardDeleteData>
}
