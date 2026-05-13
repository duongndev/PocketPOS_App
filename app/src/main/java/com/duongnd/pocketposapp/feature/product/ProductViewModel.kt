package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("Tất cả")

    val productsPagingData: Flow<PagingData<Product>> = combine(
        _searchQuery,
        _selectedCategory
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        repository.getRemoteProductsPager(
            search = query.ifEmpty { null },
            category = category,
            onTotalItemsFetched = { total ->
                _state.update { it.copy(totalProducts = total) }
            }
        )
    }.cachedIn(viewModelScope)

    private var searchJob: Job? = null

    init {
        loadProducts()
    }

    fun loadProducts(query: String? = null, category: String? = null) {
        viewModelScope.launch {
            val currentQuery = query ?: _state.value.searchQuery
            val currentCategory = category ?: _state.value.selectedCategory
            
            _state.update { it.copy(isLoading = true, searchQuery = currentQuery, selectedCategory = currentCategory) }
            try {
                // Sử dụng API hỗ trợ search, trả về cả danh sách và tổng số mục từ pagination
                val (remoteProducts, totalItems) = repository.getRemoteProducts(search = currentQuery.ifEmpty { null })
                
                val filteredProducts = if (currentCategory == "Tất cả") {
                    remoteProducts
                } else {
                    remoteProducts.filter { it.categoryName == currentCategory }
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        products = filteredProducts,
                        totalProducts = totalItems, // Lấy từ totalItems trong pagination
                        lowStockCount = filteredProducts.count { p -> p.variants.any { v -> v.stock < 10 } }
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                        products = emptyList()
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            _searchQuery.value = query
        }
    }

    fun onCategoryChange(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        _selectedCategory.value = category
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
