package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.safeApiCall
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDTO
import com.duongnd.pocketposapp.data.remote.mapper.toDomain
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductAPI
) : ProductRepository {

    override suspend fun getProducts(
        page: Int,
        limit: Int,
        search: String?
    ): Result<List<Product>> {
        return safeApiCall(
            apiCall = { api.getProducts(page, limit, search) },
            mapper = { response -> response.products.map { it.toDomain() } }
        )
    }

    override suspend fun getProductById(id: String): Result<Product> {
        return safeApiCall(
            apiCall = { api.getProductById(id) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun getProductByBarcode(barcode: String): Result<Product> {
        return safeApiCall(
            apiCall = { api.getProductByBarcode(barcode) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        return safeApiCall(
            apiCall = { api.createProduct(product.toDTO()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateProduct(id: String, product: Product): Result<Product> {
        return safeApiCall(
            apiCall = { api.updateProduct(id, product.toDTO()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteProduct(id: String): Result<Unit> {
        return safeApiCall(
            apiCall = { api.deleteProduct(id) },
            mapper = { Unit }
        )
    }
}
