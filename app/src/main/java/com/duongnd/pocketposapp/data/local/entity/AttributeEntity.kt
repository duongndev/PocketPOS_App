package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attributes")
data class AttributeEntity(
    @PrimaryKey(autoGenerate = true)
    val attributeId: Int = 0,
    val productId: Int, // Liên kết với ProductEntity
    val name: String // Ví dụ: "Màu sắc", "Kích thước", "Quy cách"
)
