package com.duongnd.pocketposapp.feature.statistics

import com.duongnd.pocketposapp.data.remote.dto.statistics.StatisticsResponse

data class StatisticsState(
    val isLoading: Boolean = false,
    val data: StatisticsResponse? = null,
    val error: String? = null,
    val selectedTab: Int = 0
)
