package com.duongnd.pocketposapp.domain.repository

import androidx.paging.PagingData
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.VariantDisplayItem
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    // Sản phẩm
    fun getProducts(): Flow<List<Product>>
    suspend fun getProductById(id: String): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun upsertProduct(product: Product)
    suspend fun deleteProduct(id: String)
    
    // Remote Sản phẩm
    suspend fun getRemoteProducts(
        page: Int = 1,
        limit: Int = 10,
        search: String? = null
    ): Pair<List<Product>, Int> // Trả về danh sách và tổng số mục

    fun getRemoteProductsPager(
        search: String? = null,
        category: String? = null,
        onTotalItemsFetched: (Int) -> Unit = {}
    ): Flow<PagingData<Product>>

    fun getRemoteProductVariantsPager(
        search: String? = null
    ): Flow<PagingData<VariantDisplayItem>>

    // Danh mục
    fun getCategories(): Flow<List<Category>>
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
