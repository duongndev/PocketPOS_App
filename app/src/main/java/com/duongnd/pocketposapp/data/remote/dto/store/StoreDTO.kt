package com.duongnd.pocketposapp.data.remote.dto.store

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StoreDTO(
    @field:Json(name = "_id")
    val id: String,
    val storeName: String,
    val description: String?,
    val phoneNumber: String?,
    val address: String?,
    val logoUrl: String?,
    val bankName: String?,
    val bankAccountNumber: String?,
    val bankAccountName: String?,
    val isActive: Boolean,
    val ownerId: String,
    val isCompleteProfile: Boolean,
    val createdAt: String,
    val updatedAt: String,
)