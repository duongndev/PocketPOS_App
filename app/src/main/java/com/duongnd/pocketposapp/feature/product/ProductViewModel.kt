package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadProducts()
    }

    fun loadProducts(query: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, searchQuery = query ?: it.searchQuery) }
            try {
                val remoteProducts = repository.getRemoteProducts(search = query)
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = remoteProducts,
                        totalProducts = remoteProducts.size,
                        lowStockCount = remoteProducts.count { p -> p.variants.any { v -> v.stock < 10 } }
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            loadProducts(query)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                loadProducts(_state.value.searchQuery)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
