package com.duongnd.pocketposapp.data.local.dao

import androidx.room.*
import com.duongnd.pocketposapp.data.local.entity.*

@Dao
interface AttributeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttribute(attribute: AttributeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttributeValue(value: AttributeValueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariantAttributeCrossRef(crossRef: VariantAttributeValueCrossRef)

    @Query("SELECT * FROM attributes WHERE productId = :productId")
    suspend fun getAttributesByProduct(productId: Int): List<AttributeEntity>

    @Query("SELECT * FROM attribute_values WHERE attributeId = :attributeId")
    suspend fun getValuesByAttribute(attributeId: Int): List<AttributeValueEntity>

    @Transaction
    @Query("""
        SELECT av.* FROM attribute_values av
        INNER JOIN variant_attribute_values vav ON av.valueId = vav.valueId
        WHERE vav.variantId = :variantId
    """)
    suspend fun getValuesForVariant(variantId: Int): List<AttributeValueEntity>
}
