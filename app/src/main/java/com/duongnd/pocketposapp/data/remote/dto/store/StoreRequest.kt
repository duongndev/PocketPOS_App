package com.duongnd.pocketposapp.data.remote.dto.store

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoreRequest (
    val storeName: String?,
    val description: String?,
    val phoneNumber: String?,
    val address: String?,
    val logoUrl: String?,
    val bankName: String?,
    val bankAccountNumber: String?,
    val bankAccountName: String?
)