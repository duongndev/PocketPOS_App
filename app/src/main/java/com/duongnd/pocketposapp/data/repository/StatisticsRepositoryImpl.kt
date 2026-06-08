package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.safeActionCallRaw
import com.duongnd.pocketposapp.data.remote.api.StatisticsAPI
import com.duongnd.pocketposapp.data.remote.dto.statistics.StatisticsResponse
import com.duongnd.pocketposapp.domain.repository.StatisticsRepository
import com.squareup.moshi.Moshi
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val statisticsAPI: StatisticsAPI,
    private val moshi: Moshi
) : StatisticsRepository {
    override suspend fun getStatisticsDashboard(period: String): Result<StatisticsResponse> {
        return safeActionCallRaw(moshi) {
            statisticsAPI.getStatisticsDashboardApi(period)
        }
    }
}