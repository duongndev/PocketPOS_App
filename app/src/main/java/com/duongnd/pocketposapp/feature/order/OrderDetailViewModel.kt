package com.duongnd.pocketposapp.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.data.remote.dto.order.OrderDetailDTO
import com.duongnd.pocketposapp.data.remote.dto.store.StoreDTO
import com.duongnd.pocketposapp.domain.repository.OrderRepository
import com.duongnd.pocketposapp.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderDetailState(
    val orderDetail: OrderDetailDTO? = null,
    val store: StoreDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OrderDetailState())
    val state: StateFlow<OrderDetailState> = _state.asStateFlow()

    init {
        _state.update { it.copy(store = storeRepository.getStoreLocal()) }
    }

    fun getOrderById(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = orderRepository.getOrderById(id)
            result.onSuccess { orderDetail ->
                _state.update { it.copy(orderDetail = orderDetail, isLoading = false) }
            }.onFailure { exception ->
                _state.update { it.copy(error = exception.message, isLoading = false) }
            }
        }
    }
}
