package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val productId: String? = savedStateHandle["productId"]

    init {
        loadProduct()
    }

    fun loadProduct() {
        productId?.let { id ->
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                try {
                    val product = repository.getProductById(id)
                    if (product != null) {
                        _state.update { it.copy(isLoading = false, product = product, error = null) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = "Không tìm thấy sản phẩm") }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}
