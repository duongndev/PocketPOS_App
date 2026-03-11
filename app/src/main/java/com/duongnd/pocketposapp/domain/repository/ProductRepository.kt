package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Sản phẩm
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun upsertProduct(product: Product)
    suspend fun deleteProduct(id: Int)

    // Danh mục
    fun getCategories(): Flow<List<Category>>
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
