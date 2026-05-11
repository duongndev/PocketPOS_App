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
    val selectedCategoryId: String? = null,
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

    private val productId: String? = savedStateHandle.get<String>("productId")

    init {
        loadCategories()
        productId?.let { if (it != "-1" && it.isNotEmpty()) loadProduct(it) } ?: run {
            // For new product, start with one default variant
            _state.update { it.copy(variants = listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = ""))) }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
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
    fun onCategorySelect(id: String) = _state.update { it.copy(selectedCategoryId = id) }
    
    fun onHasVariantsChange(has: Boolean) {
        _state.update { s -> 
            val newVariants = if (has) {
                // If switching TO variants, clear current variants and start fresh with attributes
                emptyList()
            } else {
                // If switching OFF variants, keep the first one as default or create new default
                if (s.variants.isNotEmpty()) {
                    listOf(s.variants.first().copy(attributes = emptyList()))
                } else {
                    listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = productId ?: ""))
                }
            }
            s.copy(
                hasVariants = has,
                variants = newVariants,
                attributes = if (has) s.attributes else emptyList() // Clear attributes if turning off
            )
        }
    }

    fun addAttribute() {
        val currentAttributes = _state.value.attributes.toMutableList()
        currentAttributes.add(AttributeInput())
        _state.update { it.copy(attributes = currentAttributes) }
    }

    fun updateAttributeName(index: Int, name: String) {
        val currentAttributes = _state.value.attributes.toMutableList()
        if (index in currentAttributes.indices) {
            currentAttributes[index] = currentAttributes[index].copy(name = name)
            _state.update { it.copy(attributes = currentAttributes) }
        }
    }

    fun addAttributeValue(index: Int, value: String) {
        if (value.isBlank()) return
        val currentAttributes = _state.value.attributes.toMutableList()
        if (index in currentAttributes.indices) {
            val currentValues = currentAttributes[index].values.toMutableList()
            if (!currentValues.contains(value)) {
                currentValues.add(value)
                currentAttributes[index] = currentAttributes[index].copy(values = currentValues)
                _state.update { it.copy(attributes = currentAttributes) }
                generateVariants()
            }
        }
    }

    fun removeAttribute(index: Int) {
        val currentAttributes = _state.value.attributes.toMutableList()
        if (index in currentAttributes.indices) {
            currentAttributes.removeAt(index)
            _state.update { it.copy(attributes = currentAttributes) }
            generateVariants()
        }
    }

    fun removeAttributeValue(attrIndex: Int, valueIndex: Int) {
        val currentAttributes = _state.value.attributes.toMutableList()
        if (attrIndex in currentAttributes.indices) {
            val currentValues = currentAttributes[attrIndex].values.toMutableList()
            if (valueIndex in currentValues.indices) {
                currentValues.removeAt(valueIndex)
                currentAttributes[attrIndex] = currentAttributes[attrIndex].copy(values = currentValues)
                _state.update { it.copy(attributes = currentAttributes) }
                generateVariants()
            }
        }
    }

    private fun generateVariants() {
        val attributes = _state.value.attributes.filter { it.name.isNotBlank() && it.values.isNotEmpty() }
        if (attributes.isEmpty()) {
            _state.update { it.copy(variants = emptyList()) }
            return
        }

        // Cartesian product
        val combinations = attributes.fold(listOf(listOf<Pair<String, String>>())) { acc, attr ->
            acc.flatMap { list ->
                attr.values.map { value -> list + (attr.name to value) }
            }
        }

        val oldVariants = _state.value.variants
        val newVariants = combinations.map { combination ->
            val attrs = combination.map { VariantAttribute(it.first, it.second) }
            // Cố gắng giữ lại dữ liệu cũ nếu thuộc tính khớp
            val existing = oldVariants.find { v ->
                v.attributes.size == attrs.size && v.attributes.all { a -> 
                    attrs.any { it.attributeName == a.attributeName && it.value == a.value }
                }
            }
            
            existing ?: ProductVariant(
                productId = productId ?: "",
                price = 0.0,
                costPrice = 0.0,
                stock = 0,
                attributes = attrs
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

    fun updateVariantBarcode(index: Int, barcode: String) {
        val currentVariants = _state.value.variants.toMutableList()
        if (index in currentVariants.indices) {
            currentVariants[index] = currentVariants[index].copy(barcode = barcode)
            _state.update { it.copy(variants = currentVariants) }
        }
    }

    fun saveProduct() {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Vui lòng nhập tên sản phẩm") }
            return
        }
        if (s.selectedCategoryId == null) {
            _state.update { it.copy(error = "Vui lòng chọn danh mục") }
            return
        }
        if (s.hasVariants && s.variants.isEmpty()) {
            _state.update { it.copy(error = "Sản phẩm biến thể phải có ít nhất một thuộc tính và giá trị") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val product = Product(
                    id = if (productId == "-1" || productId == null) "" else productId,
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
