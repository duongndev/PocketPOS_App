package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import com.duongnd.pocketposapp.domain.repository.CartRepository
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
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
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductState())
    val state: StateFlow<ProductState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow("Tất cả")

    val productsPagingData: Flow<PagingData<Product>> = combine(
        _searchQuery,
        _selectedCategoryId
    ) { query, categoryId ->
        Pair(query, categoryId)
    }.flatMapLatest { (query, categoryId) ->
        repository.getRemoteProductsPager(
            search = query.ifEmpty { null },
            categoryId = if (categoryId == "Tất cả") null else categoryId,
            onTotalItemsFetched = { total ->
                _state.update { it.copy(totalProducts = total) }
            }
        )
    }.cachedIn(viewModelScope)

    private var searchJob: Job? = null

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                // Assuming getRemoteCategories returns a CategoryPage which has data (List<Category>)
                val categoryPage = categoryRepository.getRemoteCategories(page = 1, limit = 100)
                _state.update { it.copy(categories = categoryPage.categories) }
            } catch (e: Exception) {
                // Fallback to local if needed, or handle error
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

    fun onCategoryChange(categoryId: String, categoryName: String) {
        _state.update { it.copy(selectedCategoryId = categoryId, selectedCategoryName = categoryName) }
        _selectedCategoryId.value = categoryId
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = repository.deleteProduct(productId)
            if (result.isSuccess) {
                // Paging data will automatically refresh if we trigger a refresh or if we use a Room-backed pager.
                // For now, we might need a way to manually refresh or the UI will do it.
                _state.update { it.copy(isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addToCart(
                ScannedItem(
                    productId = product.id,
                    barcode = product.barcode ?: "",
                    name = product.name,
                    price = product.sellingPrice,
                    count = 1
                )
            )
        }
    }
}
