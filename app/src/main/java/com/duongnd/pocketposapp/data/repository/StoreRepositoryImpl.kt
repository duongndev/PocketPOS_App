package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.core.utils.safeActionCallRaw
import com.duongnd.pocketposapp.data.remote.api.StoreAPI
import com.duongnd.pocketposapp.data.remote.api.VietQRAPI
import com.duongnd.pocketposapp.data.remote.dto.store.BankItem
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreRequest
import com.duongnd.pocketposapp.domain.repository.StoreRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val storeAPI: StoreAPI,
    private val vietQRAPI: VietQRAPI,
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

    override suspend fun getBanks(): Result<List<BankItem>> {
        return try {
            val cachedBanks = sharedPrefs.getBanks()
            if (!cachedBanks.isNullOrEmpty()) {
                Result.success(cachedBanks)
            } else {
                val response = vietQRAPI.getBanks()
                val banks = response.data ?: emptyList()
                if (banks.isNotEmpty()) {
                    sharedPrefs.saveBanks(banks)
                }
                Result.success(banks)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}