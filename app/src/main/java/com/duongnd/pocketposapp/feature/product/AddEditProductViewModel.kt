package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.*
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class AddEditProductState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val name: String = "",
    val description: String = "",
    val selectedCategoryId: Int? = null,
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

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditProductState())
    val state = _state.asStateFlow()

    private val productId: Int? = savedStateHandle.get<Int>("productId")

    init {
        loadCategories()
        productId?.let { if (it != -1) loadProduct(it) }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadProduct(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val product = repository.getProductById(id)
            product?.let { p ->
                // Map existing attributes if needed (simplified here)
                _state.update { it.copy(
                    isLoading = false,
                    name = p.name,
                    description = p.description ?: "",
                    selectedCategoryId = p.categoryId,
                    hasVariants = p.hasVariants,
                    variants = p.variants
                ) }
            } ?: _state.update { it.copy(isLoading = false) }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc) }
    fun onCategorySelect(id: Int) = _state.update { it.copy(selectedCategoryId = id) }
    fun onHasVariantsChange(has: Boolean) {
        _state.update { it.copy(hasVariants = has) }
        if (!has && _state.value.variants.isEmpty()) {
            // Create a default variant if no variants exist and hasVariants is false
            _state.update { it.copy(variants = listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = productId ?: 0))) }
        } else if (!has && _state.value.variants.size > 1) {
            // If turning off variants, keep only the first one or reset to default
             _state.update { it.copy(variants = listOf(_state.value.variants.first().copy(attributes = emptyList()))) }
        }
    }

    fun addAttribute() {
        val currentAttributes = _state.value.attributes.toMutableList()
        currentAttributes.add(AttributeInput())
        _state.update { it.copy(attributes = currentAttributes) }
    }

    fun updateAttributeName(index: Int, name: String) {
        val currentAttributes = _state.value.attributes.toMutableList()
        currentAttributes[index] = currentAttributes[index].copy(name = name)
        _state.update { it.copy(attributes = currentAttributes) }
    }

    fun addAttributeValue(index: Int, value: String) {
        if (value.isBlank()) return
        val currentAttributes = _state.value.attributes.toMutableList()
        val currentValues = currentAttributes[index].values.toMutableList()
        if (!currentValues.contains(value)) {
            currentValues.add(value)
            currentAttributes[index] = currentAttributes[index].copy(values = currentValues)
            _state.update { it.copy(attributes = currentAttributes) }
            generateVariants()
        }
    }

    private fun generateVariants() {
        val attributes = _state.value.attributes.filter { it.name.isNotBlank() && it.values.isNotEmpty() }
        if (attributes.isEmpty()) return

        // Cartesian product of attribute values
        val combinations = attributes.fold(listOf(listOf<Pair<String, String>>())) { acc, attr ->
            acc.flatMap { list ->
                attr.values.map { value -> list + (attr.name to value) }
            }
        }

        val newVariants = combinations.map { combination ->
            val variantName = combination.joinToString(" - ") { it.second }
            ProductVariant(
                productId = productId ?: 0,
                price = 0.0,
                costPrice = 0.0,
                stock = 0,
                attributes = combination.map { VariantAttribute(it.first, it.second) }
            )
        }
        _state.update { it.copy(variants = newVariants) }
    }

    fun updateVariant(index: Int, price: Double, costPrice: Double, stock: Int, sku: String?, barcode: String?) {
        val currentVariants = _state.value.variants.toMutableList()
        if (index in currentVariants.indices) {
            currentVariants[index] = currentVariants[index].copy(
                price = price,
                costPrice = costPrice,
                stock = stock,
                sku = sku,
                barcode = barcode
            )
            _state.update { it.copy(variants = currentVariants) }
        }
    }

    fun updateBarcodeForVariant(index: Int, barcode: String) {
        val currentVariants = _state.value.variants.toMutableList()
        if (index in currentVariants.indices) {
            currentVariants[index] = currentVariants[index].copy(barcode = barcode)
            _state.update { it.copy(variants = currentVariants) }
        }
    }

    fun updateBarcodeForDefault(barcode: String) {
        if (_state.value.variants.isNotEmpty()) {
            updateVariantBarcode(0, barcode)
        } else {
             _state.update { it.copy(variants = listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = productId ?: 0, barcode = barcode))) }
        }
    }
    
    fun updateVariantBarcode(index: Int, barcode: String) {
        val currentVariants = _state.value.variants.toMutableList()
        if (index in currentVariants.indices) {
            currentVariants[index] = currentVariants[index].copy(barcode = barcode)
            _state.update { it.copy(variants = currentVariants) }
        }
    }

    fun saveProduct() {
        val s = _state.value
        if (s.name.isBlank() || s.selectedCategoryId == null) {
            _state.update { it.copy(error = "Vui lòng nhập đầy đủ thông tin bắt buộc") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val product = Product(
                    id = if (productId == -1) 0 else (productId ?: 0),
                    name = s.name,
                    description = s.description,
                    categoryId = s.selectedCategoryId,
                    hasVariants = s.hasVariants,
                    variants = s.variants,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                repository.upsertProduct(product)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
