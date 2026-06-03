package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.core.utils.safeActionCallRaw
import com.duongnd.pocketposapp.data.remote.api.StoreAPI
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest
import com.duongnd.pocketposapp.domain.repository.StoreRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeAPI: StoreAPI,
    private val sharedPrefs: ShareReferenceManager,
    private val moshi: Moshi
) : StoreRepository {
    override suspend fun getStoreProfile(): Result<StoreDTO> {
        return safeActionCallRaw(moshi) {
            storeAPI.getStoreProfile()
        }.onSuccess { store ->
            sharedPrefs.saveStore(store)
        }
    }

    override suspend fun updateStoreProfile(storeRequest: StoreRequest): Result<StoreDTO> {
        return safeActionCallRaw(moshi) {
            storeAPI.updateStoreProfile(storeRequest)
        }.onSuccess { store -> sharedPrefs.saveStore(store) }
    }

    override fun getStoreLocal(): StoreDTO? {
        return sharedPrefs.getStore()
    }

}