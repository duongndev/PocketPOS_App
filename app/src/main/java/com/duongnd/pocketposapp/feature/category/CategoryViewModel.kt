package com.duongnd.pocketposapp.feature.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            categoryRepository.getCategories(page = 1, limit = 10)
                .onSuccess { categories ->
                    _state.update { it.copy(isLoading = false, categories = categories) }

                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            // Trong thực tế có thể gọi API search riêng, ở đây ta load lại list hoặc filter
            loadCategories()
        }
    }

    fun onShowBottomSheet(show: Boolean, category: Category? = null) {
        _state.update { it.copy(showBottomSheet = show, selectedCategory = category) }
    }

    fun onRevealedCategoryChange(id: String?) {
        _state.update { it.copy(revealedCategoryId = id) }
    }

    fun saveCategory(name: String, description: String) {
        val currentSelected = _state.value.selectedCategory
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (currentSelected != null) {
                categoryRepository.updateCategory(
                    currentSelected.id,
                    currentSelected.copy(name = name, description = description)
                )
            } else {
                categoryRepository.createCategory(
                    Category(
                        id = "", 
                        name = name,
                        description = description,
                        isActive = true,
                        createdAt = "",
                        updatedAt = ""
                    )
                )
            }

            result.onSuccess {
                _state.update { it.copy(showBottomSheet = false, selectedCategory = null) }
                loadCategories()
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category.id)
                .onSuccess {
                    loadCategories()
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }
}
