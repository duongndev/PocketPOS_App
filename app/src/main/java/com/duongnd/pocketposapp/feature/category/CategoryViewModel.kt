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

sealed class CategoryUiEvent {
    object Refresh : CategoryUiEvent()
    data class ShowSnackbar(val message: String) : CategoryUiEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    private val _sortBy = MutableStateFlow<String?>(null)
    private val _sortOrder = MutableStateFlow<String?>(null)
    
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CategoryUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val categoriesPagingData: Flow<PagingData<Category>> = combine(_sortBy, _sortOrder) { sortBy, sortOrder ->
        Pair(sortBy, sortOrder)
    }.flatMapLatest { (sortBy, sortOrder) ->
            categoryRepository.getRemoteCategoriesPager(
                sortBy = sortBy,
                sortOrder = sortOrder
            )
        }.cachedIn(viewModelScope)

    fun onSortChange(sortBy: String?, sortOrder: String?) {
        _sortBy.value = sortBy
        _sortOrder.value = sortOrder
    }

    fun onShowBottomSheet(show: Boolean, category: Category? = null) {
        _state.update { it.copy(showBottomSheet = show, selectedCategory = category) }
    }

    fun onRevealedCategoryChange(id: String?) {
        _state.update { it.copy(revealedCategoryId = id) }
    }

    fun onDismissResultDialog() {
        _state.update { it.copy(showResultDialog = false, resultMessage = null) }
    }

    fun saveCategory(name: String, description: String?) {
        val currentSelected = state.value.selectedCategory

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                if (currentSelected != null) {
                    categoryRepository.updateCategory(
                        id = currentSelected.id,
                        name = name,
                        description = description
                    )
                } else {
                    categoryRepository.createCategory(
                        name = name,
                        description = description
                    )
                }
                _state.update {
                    it.copy(
                        showBottomSheet = false,
                        isLoading = false,
                        showResultDialog = true,
                        isSuccess = true,
                        resultMessage = if (currentSelected != null) "Cập nhật thể loại thành công" else "Thêm thể loại thành công"
                    )
                }
                _uiEvent.emit(CategoryUiEvent.Refresh)
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        showBottomSheet = false,
                        isLoading = false,
                        showResultDialog = true,
                        isSuccess = false,
                        resultMessage = e.message ?: "Đã xảy ra lỗi"
                    )
                }
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                categoryRepository.deleteCategory(categoryId)
                _state.update { it.copy(isLoading = false) }
                _uiEvent.emit(CategoryUiEvent.Refresh)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
