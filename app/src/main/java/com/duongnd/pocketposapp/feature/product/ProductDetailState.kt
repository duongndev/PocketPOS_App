package com.duongnd.pocketposapp.feature.product

import com.duongnd.pocketposapp.domain.model.Product

data class ProductDetailState(
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val product: Product? = null,
    val error: String? = null
)

sealed interface ProductDetailUiEvent {
    data object NavigateBack : ProductDetailUiEvent
    data class ShowToast(val message: String) : ProductDetailUiEvent
}

sealed interface ProductDetailAction {
    data object Refresh : ProductDetailAction
    data object DeleteProduct : ProductDetailAction
}

