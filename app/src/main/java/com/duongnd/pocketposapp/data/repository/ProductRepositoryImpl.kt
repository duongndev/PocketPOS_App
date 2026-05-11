package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.data.local.dao.AttributeDao
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.dao.ProductDao
import com.duongnd.pocketposapp.data.local.mapper.toDomain
import com.duongnd.pocketposapp.data.local.mapper.toEntity
import com.duongnd.pocketposapp.data.remote.api.ProductAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDomainModel
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.model.Product
import com.duongnd.pocketposapp.domain.model.VariantAttribute
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val attributeDao: AttributeDao,
    private val productAPI: ProductAPI
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getProductsWithVariants().map { list ->
            list.map { item ->
                val variants = item.variants.map { variantEntity ->
                    val domainVariant = variantEntity.toDomain()
                    // Lấy các thuộc tính cho từng biến thể
                    val attributeValues = attributeDao.getValuesForVariant(variantEntity.variantId)
                    val attributes = attributeValues.map { valueEntity ->
                        // Tìm attribute name tương ứng (Ví dụ: Màu sắc)
                        val attribute = attributeDao.getAttributesByProduct(item.product.id)
                            .find { it.attributeId == valueEntity.attributeId }
                        VariantAttribute(
                            attributeName = attribute?.name ?: "",
                            value = valueEntity.value
                        )
                    }
                    domainVariant.copy(attributes = attributes)
                }
                item.product.toDomain(variants)
            }
        }
    }

    override suspend fun getProductById(id: String): Product? {
        val item = productDao.getProductWithVariantsById(id.toIntOrNull() ?: 0) ?: return null
        val variants = item.variants.map { variantEntity ->
            val domainVariant = variantEntity.toDomain()
            val attributeValues = attributeDao.getValuesForVariant(variantEntity.variantId)
            val attributes = attributeValues.map { valueEntity ->
                val attribute = attributeDao.getAttributesByProduct(item.product.id)
                    .find { it.attributeId == valueEntity.attributeId }
                VariantAttribute(
                    attributeName = attribute?.name ?: "",
                    value = valueEntity.value
                )
            }
            domainVariant.copy(attributes = attributes)
        }
        return item.product.toDomain(variants)
    }

    override suspend fun getProductByBarcode(barcode: String): Product? {
        val item = productDao.getProductWithVariantsByBarcode(barcode) ?: return null
        val variants = item.variants.map { variantEntity ->
            val domainVariant = variantEntity.toDomain()
            val attributeValues = attributeDao.getValuesForVariant(variantEntity.variantId)
            val attributes = attributeValues.map { valueEntity ->
                val attribute = attributeDao.getAttributesByProduct(item.product.id)
                    .find { it.attributeId == valueEntity.attributeId }
                VariantAttribute(
                    attributeName = attribute?.name ?: "",
                    value = valueEntity.value
                )
            }
            domainVariant.copy(attributes = attributes)
        }
        return item.product.toDomain(variants)
    }

    override suspend fun upsertProduct(product: Product) {
        val productId = productDao.insertProduct(product.toEntity()).toInt()
        
        // Xử lý các biến thể nếu có
        product.variants.forEach { variant ->
            val variantId = productDao.insertVariant(variant.toEntity().copy(productId = productId)).toInt()
            
            // Lưu các thuộc tính của biến thể
            variant.attributes.forEach { attr ->
                // Đoạn này thực tế cần logic phức tạp hơn để kiểm tra attribute đã tồn tại chưa
                // Ở mức độ cơ bản, ta giả định attribute và value đã được tạo
            }
        }
    }

    override suspend fun deleteProduct(id: String) {
        productDao.deleteProduct(id.toIntOrNull() ?: 0)
    }

    override suspend fun getRemoteProducts(page: Int, limit: Int, search: String?): List<Product> {
        return try {
            val response = productAPI.getProducts(page, limit, search)
            if (response.success) {
                response.data.products.map { it.toDomainModel() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun upsertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }
}
