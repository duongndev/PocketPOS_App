package com.duongnd.pocketposapp.data.remote.api

import com.duongnd.pocketposapp.data.remote.dto.ActionResponse
import com.duongnd.pocketposapp.data.remote.dto.statistics.StatisticsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface StatisticsAPI {
    @GET("/api/statistics/dashboard")
    suspend fun getStatisticsDashboardApi(
        @Query("period") period: String,
    ): ActionResponse<StatisticsResponse>
}