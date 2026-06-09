package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.*
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditProductState())
    val state = _state.asStateFlow()

    private val productId: String? = savedStateHandle.get<String>("productId")

    init {
        loadCategories()
        productId?.let { if (it != "-1" && it.isNotEmpty()) loadProduct(it) }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val remoteData = categoryRepository.getRemoteCategories(page = 1, limit = 100)
                _state.update { it.copy(categories = remoteData.categories) }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val product = repository.getProductById(id)
            product?.let { p ->
                _state.update { it.copy(
                    isLoading = false,
                    name = p.name,
                    barcode = p.barcode ?: "",
                    brand = p.brand ?: "",
                    costPrice = p.costPrice.toString(),
                    sellingPrice = p.sellingPrice.toString(),
                    stock = p.stock.toString(),
                    unit = p.unit ?: "",
                    description = p.description ?: "",
                    selectedCategoryId = p.categoryId,
                    imageUri = p.imageUrl
                ) }
            } ?: _state.update { it.copy(isLoading = false) }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }
    fun onBarcodeChange(barcode: String) = _state.update { it.copy(barcode = barcode) }
    fun onBrandChange(brand: String) = _state.update { it.copy(brand = brand) }
    fun onCostPriceChange(price: String) = _state.update { it.copy(costPrice = price) }
    fun onSellingPriceChange(price: String) = _state.update { it.copy(sellingPrice = price) }
    fun onStockChange(stock: String) = _state.update { it.copy(stock = stock) }
    fun onUnitChange(unit: String) = _state.update { it.copy(unit = unit) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc) }
    fun onCategorySelect(id: String) = _state.update { it.copy(selectedCategoryId = id) }
    fun onImageChange(uri: String?) = _state.update { it.copy(imageUri = uri) }

    fun saveProduct() {
        val s = _state.value
        if (s.name.isBlank()) { _state.update { it.copy(error = "Tên sản phẩm không được để trống") }; return }
        if (s.selectedCategoryId == null) { _state.update { it.copy(error = "Vui lòng chọn danh mục") }; return }

        viewModelScope.launch {
            productId?.let { id ->
                try {
                    _state.update { it.copy(isLoading = true) }
                    val product = Product(
                        id = id,
                        name = s.name.trim(),
                        categoryId = s.selectedCategoryId,
                        barcode = s.barcode.trim(),
                        brand = s.brand.trim(),
                        imageUrl = s.imageUri,
                        costPrice = s.costPrice.toDoubleOrNull() ?: 0.0,
                        sellingPrice = s.sellingPrice.toDoubleOrNull() ?: 0.0,
                        stock = s.stock.toIntOrNull() ?: 0,
                        unit = s.unit.trim(),
                        description = s.description.trim()
                    )
                    val result = repository.updateProduct(id, product)
                    if (result.isSuccess) {
                        _state.update { it.copy(isLoading = false, isSaved = true) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }

    fun deleteProduct() {
        productId?.let { id ->
            viewModelScope.launch {
                try {
                    _state.update { it.copy(isLoading = true) }
                    val result = repository.deleteProduct(id)
                    if (result.isSuccess) {
                        _state.update { it.copy(isLoading = false, isSaved = true) }
                    } else {
                        _state.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}
