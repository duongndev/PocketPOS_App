package com.duongnd.pocketposapp.feature.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duongnd.pocketposapp.domain.model.*
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditProductState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
        // Khởi tạo một biến thể mặc định cho sản phẩm mới
        _state.update { it.copy(variants = listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = ""))) }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val remoteData = categoryRepository.getRemoteCategories(page = 1, limit = 100)
                if (remoteData.categories.isNotEmpty()) {
                    _state.update { it.copy(categories = remoteData.categories) }
                } else {
                    loadLocalCategories()
                }
            } catch (e: Exception) {
                loadLocalCategories()
            }
        }
    }

    private fun loadLocalCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }
    fun onDescriptionChange(desc: String) = _state.update { it.copy(description = desc) }
    fun onCategorySelect(id: String) = _state.update { it.copy(selectedCategoryId = id) }
    fun onImageChange(uri: String?) = _state.update { it.copy(imageUri = uri) }

    fun createCategory(name: String) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val newCategory = categoryRepository.createCategory(name, "", null, null)
                loadCategories()
                _state.update { it.copy(selectedCategoryId = newCategory.id, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onHasVariantsChange(has: Boolean) {
        _state.update { s -> 
            val newVariants = if (has) emptyList() else listOf(ProductVariant(price = 0.0, costPrice = 0.0, stock = 0, productId = ""))
            s.copy(hasVariants = has, variants = newVariants, attributes = if (has) s.attributes else emptyList())
        }
    }

    fun addAttribute() {
        val current = _state.value.attributes.toMutableList()
        current.add(AttributeInput())
        _state.update { it.copy(attributes = current) }
    }

    fun updateAttributeName(index: Int, name: String) {
        val current = _state.value.attributes.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(name = name)
            _state.update { it.copy(attributes = current) }
        }
    }

    fun addAttributeValue(index: Int, value: String) {
        if (value.isBlank()) return
        val current = _state.value.attributes.toMutableList()
        if (index in current.indices) {
            val values = current[index].values.toMutableList()
            if (!values.contains(value)) {
                values.add(value)
                current[index] = current[index].copy(values = values)
                _state.update { it.copy(attributes = current) }
                generateVariants()
            }
        }
    }

    fun removeAttribute(index: Int) {
        val current = _state.value.attributes.toMutableList()
        if (index in current.indices) {
            current.removeAt(index); _state.update { it.copy(attributes = current) }; generateVariants()
        }
    }

    fun removeAttributeValue(attrIndex: Int, valueIndex: Int) {
        val current = _state.value.attributes.toMutableList()
        if (attrIndex in current.indices) {
            val values = current[attrIndex].values.toMutableList()
            if (valueIndex in values.indices) {
                values.removeAt(valueIndex)
                current[attrIndex] = current[attrIndex].copy(values = values)
                _state.update { it.copy(attributes = current) }; generateVariants()
            }
        }
    }

    private fun generateVariants() {
        val attributes = _state.value.attributes.filter { it.name.isNotBlank() && it.values.isNotEmpty() }
        if (attributes.isEmpty()) { _state.update { it.copy(variants = emptyList()) }; return }

        val combinations = attributes.fold(listOf(listOf<Pair<String, String>>())) { acc, attr ->
            acc.flatMap { list -> attr.values.map { value -> list + (attr.name to value) } }
        }

        val oldVariants = _state.value.variants
        val newVariants = combinations.map { combination ->
            val attrs = combination.map { VariantAttribute(it.first, it.second) }
            val existing = oldVariants.find { v -> v.attributes.size == attrs.size && v.attributes.all { a -> attrs.any { it.name == a.name && it.value == a.value } } }
            existing ?: ProductVariant(productId = "", price = 0.0, costPrice = 0.0, stock = 0, attributes = attrs)
        }
        _state.update { it.copy(variants = newVariants) }
    }

    fun updateVariant(index: Int, price: Double, costPrice: Double, stock: Int, sku: String?, barcode: String?) {
        val current = _state.value.variants.toMutableList()
        if (index in current.indices) {
            current[index] = current[index].copy(price = price, costPrice = costPrice, stock = stock, sku = sku, barcode = barcode)
            _state.update { it.copy(variants = current) }
        }
    }

    fun saveProduct() {
        val s = _state.value
        if (s.name.isBlank()) { _state.update { it.copy(error = "Tên sản phẩm không được để trống") }; return }
        if (s.selectedCategoryId == null) { _state.update { it.copy(error = "Vui lòng chọn danh mục") }; return }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                val finalVariants = if (s.hasVariants) {
                    s.variants.map { v ->
                        val autoSku = v.sku.takeIf { !it.isNullOrBlank() } ?: "${s.name.filter { it.isLetterOrDigit() }.take(5).uppercase()}-${v.attributes.joinToString("") { it.value.take(1).uppercase() }}"
                        v.copy(sku = autoSku)
                    }
                } else {
                    val defaultV = s.variants.firstOrNull() ?: ProductVariant(productId = "", price = 0.0, costPrice = 0.0, stock = 0)
                    listOf(defaultV.copy(sku = defaultV.sku.takeIf { !it.isNullOrBlank() } ?: s.name.filter { it.isLetterOrDigit() }.take(8).uppercase()))
                }

                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val product = Product(
                    id = "",
                    name = s.name.trim(),
                    description = s.description.trim(),
                    categoryId = s.selectedCategoryId,
                    imageUri = s.imageUri,
                    hasVariants = s.hasVariants,
                    options = s.attributes.map { ProductOption(it.name, it.values) },
                    variants = finalVariants,
                    createdAt = now,
                    updatedAt = now
                )
                repository.upsertProduct(product)
                _state.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
