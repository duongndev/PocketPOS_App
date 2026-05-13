package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.product.ProductDTO
import com.duongnd.pocketposapp.data.remote.dto.product.ProductRequest
import com.duongnd.pocketposapp.data.remote.dto.product.ProductVariantListData
import com.duongnd.pocketposapp.data.remote.dto.ProductListData
import retrofit2.http.*

interface   ProductAPI {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = null
    ): ApiResponse<ProductListData>

    @GET("products/variants")
    suspend fun getProductVariants(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = null
    ): ApiResponse<ProductVariantListData>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: String
    ): ApiResponse<ProductDTO>

    @GET("products/barcode/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ApiResponse<ProductDTO>

    @POST("products")
    suspend fun createProduct(
        @Body product: ProductRequest
    ): ApiResponse<ProductDTO>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductRequest
    ): ApiResponse<ProductDTO>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): ApiResponse<Unit>
}
