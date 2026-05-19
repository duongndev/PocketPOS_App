package com.duongnd.pocketposapp.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedParentId = MutableStateFlow<String?>(null)
    
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    val mainCategoriesPagingData: Flow<PagingData<Category>> = _searchQuery
        .flatMapLatest { query ->
            categoryRepository.getRemoteCategoriesPager(
                search = query.ifEmpty { null },
                isChildren = false
            )
        }.cachedIn(viewModelScope)

    val subCategoriesPagingData: Flow<PagingData<Category>> = _searchQuery
        .flatMapLatest { query ->
            categoryRepository.getRemoteCategoriesPager(
                search = query.ifEmpty { null },
                isChildren = true
            )
        }.cachedIn(viewModelScope)

    fun onParentCategorySelect(parentId: String?) {
        _selectedParentId.value = parentId
        _state.update { it.copy(selectedParentId = parentId) }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }
    }

    fun onShowBottomSheet(show: Boolean, category: Category? = null) {
        _state.update { it.copy(showBottomSheet = show, selectedCategory = category) }
        if (show) {
            fetchParentCategories()
        }
    }

    private fun fetchParentCategories() {
        viewModelScope.launch {
            try {
                // Fetch first page of categories for parent selection
                // In a real app, you might want a specialized API for this or a longer list
                val result = categoryRepository.getRemoteCategories(limit = 10, isActive = true, parentId = null)
                _state.update { it.copy(parentCategories = result.categories) }
            } catch (e: Exception) {
                // Ignore for now
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun onRevealedCategoryChange(id: String?) {
        _state.update { it.copy(revealedCategoryId = id) }
    }

    fun saveCategory(name: String, description: String, parentId: String? = null, sortOrder: Int? = 0) {
        val currentSelected = state.value.selectedCategory

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (currentSelected != null) {
                    categoryRepository.updateCategory(
                        id = currentSelected.id,
                        name = name,
                        description = description,
                        parentId = parentId,
                        sortOrder = sortOrder
                    )
                } else {
                    categoryRepository.createCategory(
                        name = name,
                        description = description,
                        parentId = parentId,
                        sortOrder = sortOrder
                    )
                }
                _state.update { it.copy(showBottomSheet = false, isLoading = false) }
                // Categories will be refreshed by Paging when UI triggers refresh
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteCategory(categoryId: String, isHardDelete: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                if (isHardDelete) {
                    categoryRepository.hardDeleteCategory(categoryId)
                } else {
                    categoryRepository.deleteCategory(categoryId)
                }
                // Categories will be refreshed by Paging when UI triggers refresh
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun checkConstraints(categoryId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val constraints = categoryRepository.getCategoryConstraints(categoryId)
                _state.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
