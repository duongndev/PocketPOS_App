package com.duongnd.pocketposapp.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.data.remote.dto.order.OrderItemRequest
import com.duongnd.pocketposapp.data.remote.dto.order.OrderRequest
import com.duongnd.pocketposapp.domain.repository.CartRepository
import com.duongnd.pocketposapp.domain.repository.OrderRepository
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val shareReferenceManager: ShareReferenceManager
) : ViewModel() {

    val scannedItems: StateFlow<List<ScannedItem>> = cartRepository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val store = shareReferenceManager.getStore()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _checkoutSuccess = MutableSharedFlow<Boolean>()
    val checkoutSuccess = _checkoutSuccess.asSharedFlow()

    fun createOrder(paymentMethod: String) {
        viewModelScope.launch {
            if (_isLoading.value) return@launch
            _isLoading.value = true
            try {
                val items = scannedItems.value.map {
                    OrderItemRequest(
                        productId = it.productId,
                        quantity = it.count
                    )
                }
                val orderRequest = OrderRequest(
                    paymentMethod = paymentMethod,
                    note = "",
                    items = items
                )
                val result = orderRepository.createOrder(orderRequest)
                result.onSuccess {
                    cartRepository.clearCart()
                    _checkoutSuccess.emit(true)
                }.onFailure {
                    _error.value = "Thanh toán thất bại: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi thanh toán: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
