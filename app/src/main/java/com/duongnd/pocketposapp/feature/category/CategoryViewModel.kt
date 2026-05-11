package com.duongnd.pocketposapp.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<Boolean?>(null)
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 0)
    private val _loadMoreTrigger = MutableSharedFlow<Unit>(replay = 0)

    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init {
        handleSearchAndPagination()
        loadRemoteCategories()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun handleSearchAndPagination() {
        merge(
            _searchQuery.debounce(500L).map { false },
            _selectedStatus.map { false },
            _refreshTrigger.map { false },
            _loadMoreTrigger.map { true }
        ).onEach { isLoadMore ->
            val currentQuery = _searchQuery.value
            val currentStatus = _selectedStatus.value
            val nextPageIndex = if (isLoadMore) state.value.nextPage ?: (state.value.currentPage + 1) else 1
            
            if (isLoadMore && !state.value.hasNextPage) return@onEach

            _state.update { 
                if (isLoadMore) it.copy(isPaginating = true)
                else it.copy(isLoading = true, searchQuery = currentQuery, selectedStatus = currentStatus)
            }

            try {
                val categoryPage = categoryRepository.getRemoteCategories(
                    page = nextPageIndex,
                    limit = 10,
                    search = currentQuery.ifEmpty { null },
                    isActive = currentStatus
                )
                
                _state.update { currentState ->
                    val newCategories = if (isLoadMore) {
                        currentState.categories + categoryPage.categories
                    } else {
                        categoryPage.categories
                    }
                    currentState.copy(
                        isLoading = false,
                        isPaginating = false,
                        categories = newCategories,
                        currentPage = categoryPage.pagination.currentPage,
                        totalPages = categoryPage.pagination.totalPages,
                        hasNextPage = categoryPage.pagination.hasNextPage,
                        nextPage = categoryPage.pagination.nextPage,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isPaginating = false, error = e.message) }
            }
        }.launchIn(viewModelScope)
    }

    fun loadRemoteCategories() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }

    fun loadNextPage() {
        if (!state.value.isLoading && !state.value.isPaginating && state.value.hasNextPage) {
            viewModelScope.launch {
                _loadMoreTrigger.emit(Unit)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusChange(isActive: Boolean?) {
        _selectedStatus.value = isActive
    }

    fun onShowBottomSheet(show: Boolean, category: Category? = null) {
        _state.update { it.copy(showBottomSheet = show, selectedCategory = category) }
    }

    fun onRevealedCategoryChange(id: String?) {
        _state.update { it.copy(revealedCategoryId = id) }
    }

    fun saveCategory(name: String, description: String, parentId: String? = null, sortOrder: Int? = null) {
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
                loadRemoteCategories()
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
                loadRemoteCategories()
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
