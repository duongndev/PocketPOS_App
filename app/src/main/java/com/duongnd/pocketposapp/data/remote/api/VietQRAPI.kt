package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.store.BankResponse
import retrofit2.http.GET

interface VietQRAPI {
    @GET("https://vietqr.app/banks.json")
    suspend fun getBanks(): BankResponse
}
