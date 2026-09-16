package com.duongnd.pocketposapp.data.remote.dto.store

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoreDTO(
    @field:Json(name = "_id")
    val id: String,
    val storeName: String,
    val description: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
    val logoUrl: String? = null,
    @field:Json(name = "bankInfo")
    val bankingInfo: BankingInfo? = null,
    val isActive: Boolean = true,
    val ownerId: String? = null,
    val isCompleteProfile: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)
