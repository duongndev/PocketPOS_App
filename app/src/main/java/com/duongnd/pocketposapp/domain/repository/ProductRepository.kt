package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(page: Int, limit: Int, search: String?): Result<List<Product>>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun getProductByBarcode(barcode: String): Result<Product>
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(id: String, product: Product): Result<Product>
    suspend fun deleteProduct(id: String): Result<Unit>
}
