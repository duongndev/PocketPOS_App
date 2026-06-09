package com.duongnd.pocketposapp.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import com.duongnd.pocketposapp.core.utils.SocketManager
import com.duongnd.pocketposapp.data.remote.dto.order.OrderCreateResponse
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

    private val _orderCreated = MutableSharedFlow<OrderCreateResponse>()
    val orderCreated = _orderCreated.asSharedFlow()

    private val _paymentSuccess = MutableSharedFlow<Unit>()
    val paymentSuccess = _paymentSuccess.asSharedFlow()

    private val _isPaymentSuccess = MutableStateFlow(false)
    val isPaymentSuccess: StateFlow<Boolean> = _isPaymentSuccess.asStateFlow()

    fun connectSocket(orderId: String) {
        val baseUrl = "https://natural-overuse-antelope.ngrok-free.dev/"
        SocketManager.connect(baseUrl)
        
        // Join rooms
        store?.id?.let { SocketManager.joinStore(it) }
        SocketManager.joinOrder(orderId)

        // Listen for payment success
        SocketManager.listenPaymentSuccess { receivedOrderId ->
            if (receivedOrderId == orderId) {
                viewModelScope.launch {
                    _isPaymentSuccess.value = true
                    _paymentSuccess.emit(Unit)
                }
            }
        }
    }

    fun disconnectSocket() {
        SocketManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnectSocket()
    }

    fun createOrder(paymentMethod: String, note: String = "") {
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
                    note = note,
                    items = items
                )
                val result = orderRepository.createOrder(orderRequest)
                result.onSuccess { response ->
                    cartRepository.clearCart()
                    _orderCreated.emit(response)
                }.onFailure {
                    _error.value = "Tạo đơn hàng thất bại: ${it.message}"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tạo đơn hàng: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
