package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ActionResponse
import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDetailDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderRequest
import com.duongnd.pocketposapp.data.remote.dto.order.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderAPI {
    @GET("/api/orders/")
    suspend fun getOrders(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ActionResponse<OrderResponse>

    @POST("/api/orders/")
    suspend fun createOrder(@Body order: OrderRequest): ActionResponse<OrderDTO>

    @GET("/api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): ActionResponse<OrderDetailDTO>
}