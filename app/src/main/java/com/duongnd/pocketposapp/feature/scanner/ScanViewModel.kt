package com.duongnd.pocketposapp.feature.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import com.duongnd.pocketposapp.domain.repository.CartRepository
import com.duongnd.pocketposapp.core.utils.ShareReferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ScannedItem(
    val productId: String,
    val barcode: String,
    val name: String,
    val price: Double,
    val count: Int
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val shareReferenceManager: ShareReferenceManager
) : ViewModel() {
    
    val scannedItems: StateFlow<List<ScannedItem>> = cartRepository.getCartItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val store = shareReferenceManager.getStore()
    val storeName: String = store?.storeName ?: "pocket pos"
    val storeAddress: String = store?.address ?: "123 ABC, Hà Nội"
    val storePhone: String = store?.phoneNumber ?: "0123456789"

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchProductByBarcode(barcode: String) {
        viewModelScope.launch {
            if (_isLoading.value) return@launch
            
            _isLoading.value = true
            try {
                val product = productRepository.getProductByBarcode(barcode)
                if (product != null) {
                    val newItem = ScannedItem(
                        productId = product.id,
                        barcode = product.barcode ?: barcode,
                        name = product.name,
                        price = product.sellingPrice,
                        count = 1
                    )
                    cartRepository.addToCart(newItem)
                    _error.value = null
                } else {
                    _error.value = "Không tìm thấy sản phẩm với mã: $barcode"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tìm kiếm sản phẩm: ${e.message}"
                Timber.e(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearBarcodes() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun increaseCount(barcode: String) {
        viewModelScope.launch {
            val item = scannedItems.value.find { it.barcode == barcode }
            if (item != null) {
                cartRepository.updateCount(barcode, item.count + 1)
            }
        }
    }

    fun decreaseCount(barcode: String) {
        viewModelScope.launch {
            val item = scannedItems.value.find { it.barcode == barcode }
            if (item != null) {
                cartRepository.updateCount(barcode, item.count - 1)
            }
        }
    }

    fun removeItem(barcode: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(barcode)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
