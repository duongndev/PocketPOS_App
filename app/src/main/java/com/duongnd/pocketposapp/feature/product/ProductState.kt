package com.duongnd.pocketposapp.feature.product

import com.duongnd.pocketposapp.domain.model.Product

data class ProductState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "Tất cả",
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0
)
