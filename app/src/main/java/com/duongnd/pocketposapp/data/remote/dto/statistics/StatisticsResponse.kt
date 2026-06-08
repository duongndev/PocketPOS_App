package com.duongnd.pocketposapp.data.remote.dto.statistics

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatisticsResponse(
    val chart: List<Chart>,
    val summary: Summary,
    val topProducts: List<TopProduct>
)