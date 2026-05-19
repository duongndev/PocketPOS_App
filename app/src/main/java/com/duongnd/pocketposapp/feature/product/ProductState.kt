package com.duongnd.pocketposapp.feature.product

import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.ProductVariant

data class ProductState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "Tất cả",
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0
)

data class AddEditProductState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val description: String = "",
    val selectedCategoryId: String? = null,
    val imageUri: String? = null,
    val hasVariants: Boolean = false,
    val attributes: List<AttributeInput> = emptyList(),
    val variants: List<ProductVariant> = emptyList(),
    val isSaved: Boolean = false,
    val error: String? = null
)

data class AttributeInput(
    val name: String = "",
    val values: List<String> = emptyList()
)
