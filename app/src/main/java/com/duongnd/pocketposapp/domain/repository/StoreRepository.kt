package com.duongnd.pocketposapp.domain.repository


import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest

interface StoreRepository {

    suspend fun getStoreProfile(): Result<StoreDTO>

    suspend fun updateStoreProfile(storeRequest: StoreRequest): Result<StoreDTO>

    fun getStoreLocal(): StoreDTO?
}