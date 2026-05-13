package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryId: Int,
    val categoryName: String = "",
    val name: String,
    val brand: String = "",
    val description: String? = null,
    val imageUri: String? = null,
    val hasVariants: Boolean = false, // true nếu có nhiều biến thể
    val createdAt: String,
    val updatedAt: String
)
