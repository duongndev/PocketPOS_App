package com.duongnd.pocketposapp.data.remote.dto.auth

import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDTO(
    @field:Json(name = "_id")
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val avatar: String? = null,
    val role: String,
    @field:Json(name = "storeId")
    val storeFromGetMe: StoreDTO? = null,
    @field:Json(name = "store")
    val storeFromLogin: StoreDTO? = null,
    val lastLoginAt: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    val store: StoreDTO? get() = storeFromGetMe ?: storeFromLogin
}
