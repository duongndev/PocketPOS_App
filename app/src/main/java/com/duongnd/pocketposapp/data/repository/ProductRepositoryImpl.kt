package com.duongnd.pocketposapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.duongnd.pocketposapp.data.paging.ProductPagingSource
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.dto.product.toRequest
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI
) : ProductRepository {

    override suspend fun getProductById(id: String): Product? {
        return try {
            val response = productAPI.getProductById(id)
            if (response.success) {
                response.data.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Product? {
        return try {
            val response = productAPI.getProductByBarcode(barcode)
            if (response.success) {
                response.data.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val response = productAPI.createProduct(product.toRequest())
            if (response.success) {
                Result.success(response.data.toDomainModel())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(id: String, product: Product): Result<Product> {
        return try {
            val response = productAPI.updateProduct(id, product.toRequest())
            if (response.success) {
                Result.success(response.data.toDomainModel())
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            val response = productAPI.deleteProduct(id)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRemoteProductsPager(
        search: String?,
        categoryId: String?,
        onTotalItemsFetched: (Int) -> Unit
    ): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductPagingSource(productAPI, search, categoryId, onTotalItemsFetched)
            }
        ).flow
    }
}
