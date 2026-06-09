package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ActionResponse
import com.duongnd.pocketposapp.data.remote.dto.ApiResponse
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT

interface StoreAPI {

    @GET("/api/stores/me")
    suspend fun getStoreProfile(): ActionResponse<StoreDTO>

    @PUT("/api/stores/me")
    suspend fun updateStoreProfile(
        @Body storeRequest: StoreRequest
    ): ActionResponse<StoreDTO>

    @PATCH("/api/stores/me/status")
    suspend fun updateStoreStatus(
        @Body storeRequest: StoreRequest
    ): ApiResponse<StoreDTO>


}