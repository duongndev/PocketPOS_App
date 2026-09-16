package com.duongnd.pocketposapp.data.remote.dto.store

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BankingInfo(
    val bankCode: String? = null,
    val bankName: String? = null,
    val accountNumber: String? = null,
    val accountHolderName: String? = null
)
