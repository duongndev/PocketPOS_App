package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ProductDetailState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<ProductDetailUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val productId: String? = savedStateHandle["productId"]

    init {
        loadProduct()
    }

    fun onAction(action: ProductDetailAction) {
        when (action) {
            ProductDetailAction.Refresh -> loadProduct()
            ProductDetailAction.DeleteProduct -> deleteProduct()
        }
    }

    fun loadProduct() {
        productId?.let { id ->
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true, error = null) }
                try {
                    val product = repository.getProductById(id)
                    if (product != null) {
                        _state.update { it.copy(isLoading = false, product = product, error = null) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Không tìm thấy sản phẩm") }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message ?: "Đã xảy ra lỗi") }
                }
            }
        }
    }

    private fun deleteProduct() {
        productId?.let { id ->
            viewModelScope.launch {
                _state.update { it.copy(isDeleting = true) }
                val result = repository.deleteProduct(id)
                _state.update { it.copy(isDeleting = false) }
                if (result.isSuccess) {
                    _uiEvent.send(ProductDetailUiEvent.NavigateBack)
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Xóa sản phẩm thất bại"
                    _state.update { it.copy(error = errorMsg) }
                    _uiEvent.send(ProductDetailUiEvent.ShowToast(errorMsg))
                }
            }
        }
    }
}

