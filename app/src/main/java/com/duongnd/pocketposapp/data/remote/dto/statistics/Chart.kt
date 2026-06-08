package com.duongnd.pocketposapp.data.remote.dto.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Chart(
    val label: String,
    val revenue: Int
)