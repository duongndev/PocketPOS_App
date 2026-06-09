package com.duongnd.pocketposapp.domain.repository

import androidx.paging.PagingData
import com.duongnd.pocketposapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProductById(id: String): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(id: String, product: Product): Result<Product>
    suspend fun deleteProduct(id: String): Result<Unit>
    
    fun getRemoteProductsPager(
        search: String? = null,
        categoryId: String? = null,
        onTotalItemsFetched: (Int) -> Unit = {}
    ): Flow<PagingData<Product>>
}
