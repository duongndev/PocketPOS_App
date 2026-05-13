package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duongnd.pocketposapp.domain.model.VariantDisplayItem
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class VariantListState(
    val searchQuery: String = ""
)

@HiltViewModel
class VariantListViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _state = MutableStateFlow(VariantListState())
    val state = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val variantsPagingData: Flow<PagingData<VariantDisplayItem>> = _searchQuery
        .flatMapLatest { query ->
            repository.getRemoteProductVariantsPager(
                search = query.ifEmpty { null }
            )
        }.cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }
    }
}
