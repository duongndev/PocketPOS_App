package com.duongnd.pocketposapp.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: StatisticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state = _state.asStateFlow()

    init {
        getStatistics("daily")
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
        val period = when (index) {
            0 -> "daily"
            1 -> "weekly"
            2 -> "monthly"
            else -> "daily"
        }
        getStatistics(period)
    }

    fun getStatistics(period: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            repository.getStatisticsDashboard(period)
                .onSuccess { response ->
                    _state.update { it.copy(isLoading = false, data = response) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}
