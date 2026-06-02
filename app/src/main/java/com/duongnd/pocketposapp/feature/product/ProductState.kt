package com.duongnd.pocketposapp.feature.product

import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product

data class ProductState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategoryId: String = "Tất cả",
    val selectedCategoryName: String = "Tất cả",
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0
)

data class AddEditProductState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val barcode: String = "",
    val brand: String = "",
    val costPrice: String = "",
    val sellingPrice: String = "",
    val stock: String = "",
    val unit: String = "",
    val description: String = "",
    val selectedCategoryId: String? = null,
    val imageUri: String? = null,
    val isSaved: Boolean = false,
    val error: String? = null
)
