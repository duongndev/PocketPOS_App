package com.duongnd.pocketposapp.data.remote.dto.statistics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopProduct(
    @field:Json(name = "_id")
    val id: String,
    val productName: String,
    val quantity: Int,
    val revenue: Int
)