package com.duongnd.pocketposapp.feature.category

import com.duongnd.pocketposapp.domain.model.Category

data class CategoryState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val showBottomSheet: Boolean = false,
    val selectedCategory: Category? = null,
    val revealedCategoryId: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasNextPage: Boolean = false,
    val nextPage: Int? = null,
    val selectedStatus: Boolean? = null
)
