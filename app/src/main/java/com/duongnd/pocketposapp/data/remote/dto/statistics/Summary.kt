package com.duongnd.pocketposapp.data.remote.dto.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Summary(
    val averageOrderValue: Int,
    val orders: Int,
    val profit: Int,
    val revenue: Int,
    val totalProducts: Int
)