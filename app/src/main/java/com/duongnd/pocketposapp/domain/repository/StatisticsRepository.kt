package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.data.remote.dto.statistics.StatisticsResponse

interface StatisticsRepository {
    suspend fun getStatisticsDashboard(period: String): Result<StatisticsResponse>
}