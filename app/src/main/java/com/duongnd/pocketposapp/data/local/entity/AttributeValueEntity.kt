package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attribute_values")
data class AttributeValueEntity(
    @PrimaryKey(autoGenerate = true)
    val valueId: Int = 0,
    val attributeId: Int, // Liên kết với AttributeEntity
    val value: String // Ví dụ: "Xanh", "A4", "70gsm"
)
