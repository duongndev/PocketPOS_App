package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.data.remote.dto.order.OrderDetailDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderRequest
import com.duongnd.pocketposapp.data.remote.dto.order.OrderResponse


interface OrderRepository {

    suspend fun getOrders(
        page: Int,
        limit: Int
    ): Result<OrderResponse>

    suspend fun createOrder(orderRequest: OrderRequest): Result<OrderDTO>

    suspend fun getOrderById(id: String): Result<OrderDetailDTO>

}