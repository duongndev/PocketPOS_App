package com.duongnd.pocketposapp.feature.product

import com.duongnd.pocketposapp.domain.model.Product

data class ProductDetailState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)
