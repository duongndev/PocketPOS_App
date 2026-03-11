package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "variant_attribute_values",
    primaryKeys = ["variantId", "valueId"]
)
data class VariantAttributeValueCrossRef(
    val variantId: Int, // Liên kết với ProductVariantEntity
    val valueId: Int    // Liên kết với AttributeValueEntity
)
