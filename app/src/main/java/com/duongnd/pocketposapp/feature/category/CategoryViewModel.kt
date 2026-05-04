package com.duongnd.pocketposapp.feature.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    private val _searchQuery = MutableStateFlow("")

    val state: StateFlow<CategoryState> = combine(
        _state,
        _searchQuery,
        categoryRepository.getCategories()
    ) { state, query, categories ->
        state.copy(
            searchQuery = query,
            categories = if (query.isEmpty()) {
                categories
            } else {
                categories.filter { 
                    it.name.contains(query, ignoreCase = true) || 
                    (it.description?.contains(query, ignoreCase = true) == true)
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryState())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onShowBottomSheet(show: Boolean, category: Category? = null) {
        _state.update { it.copy(showBottomSheet = show, selectedCategory = category) }
    }

    fun onRevealedCategoryChange(id: Int?) {
        _state.update { it.copy(revealedCategoryId = id) }
    }

    fun saveCategory(name: String, description: String) {
        val currentSelected = _state.value.selectedCategory
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            try {
                val categoryToSave = if (currentSelected != null) {
                    currentSelected.copy(
                        name = name,
                        description = description,
                        updatedAt = currentTime
                    )
                } else {
                    Category(
                        name = name,
                        description = description,
                        isActive = true,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    )
                }
                categoryRepository.upsertCategory(categoryToSave)
                onShowBottomSheet(false)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.deleteCategory(category)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}
