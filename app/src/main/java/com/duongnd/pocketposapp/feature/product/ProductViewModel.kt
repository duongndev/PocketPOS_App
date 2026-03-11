package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    private val _searchQuery = MutableStateFlow("")

    val state: StateFlow<ProductState> = combine(
        _state,
        _searchQuery,
        repository.getProducts()
    ) { state, query, products ->
        val filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.variants.any { v -> v.sku?.contains(query, ignoreCase = true) == true || v.barcode?.contains(query, ignoreCase = true) == true }
            }
        }
        state.copy(
            products = filteredProducts,
            searchQuery = query,
            totalProducts = products.size,
            lowStockCount = products.count { p -> p.variants.any { v -> v.stock < 10 } }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
