package com.duongnd.pocketposapp.data.remote.dto.order

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderCreatedByDTO(
    @field:Json(name = "_id")
    val id: String,
    val fullName: String,
)
