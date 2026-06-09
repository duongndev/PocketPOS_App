package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.core.utils.safeActionCallRaw
import com.duongnd.pocketposapp.data.remote.api.OrderAPI
import com.duongnd.pocketposapp.data.remote.api.StoreAPI
import com.duongnd.pocketposapp.data.remote.dto.order.OrderCreateResponse
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDetailDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDTO
import com.duongnd.pocketposapp.data.remote.dto.order.OrderRequest
import com.duongnd.pocketposapp.data.remote.dto.order.OrderResponse
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest
import com.duongnd.pocketposapp.domain.repository.OrderRepository
import com.duongnd.pocketposapp.domain.repository.StoreRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderAPI: OrderAPI,
    private val moshi: Moshi
) : OrderRepository {
    override suspend fun getOrders(
        page: Int,
        limit: Int
    ): Result<OrderResponse> {
        return safeActionCallRaw(moshi) {
            orderAPI.getOrders(page, limit)
        }
    }

    override suspend fun createOrder(orderRequest: OrderRequest): Result<OrderCreateResponse> {
        return safeActionCallRaw(moshi) {
            orderAPI.createOrder(orderRequest)
        }
    }

    override suspend fun getOrderById(id: String): Result<OrderDetailDTO> {
        return safeActionCallRaw(moshi) {
            orderAPI.getOrderById(id)
        }
    }

    override suspend fun confirmPayment(id: String): Result<OrderDetailDTO> {
        return safeActionCallRaw(moshi) {
            orderAPI.confirmPayment(id)
        }
    }
}