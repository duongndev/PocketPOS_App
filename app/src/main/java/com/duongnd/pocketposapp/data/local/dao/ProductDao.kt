package com.duongnd.pocketposapp.data.local.dao

import androidx.room.*
import com.duongnd.pocketposapp.data.local.entity.*
import kotlinx.coroutines.flow.Flow

// POJO để lấy Product kèm theo các biến thể của nó
data class ProductWithVariants(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val variants: List<ProductVariantEntity>
)

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: ProductVariantEntity): Long

    @Transaction
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getProductsWithVariants(): Flow<List<ProductWithVariants>>

    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductWithVariantsById(productId: Int): ProductWithVariants?

    @Transaction
    @Query("""
        SELECT * FROM products 
        WHERE id IN (SELECT productId FROM product_variants WHERE barcode = :barcode)
    """)
    suspend fun getProductWithVariantsByBarcode(barcode: String): ProductWithVariants?

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: Int)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Update
    suspend fun updateVariant(variant: ProductVariantEntity)
}
