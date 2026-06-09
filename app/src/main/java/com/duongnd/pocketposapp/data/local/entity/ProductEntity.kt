package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val storeId: String,
    val categoryId: String,
    val categoryName: String = "",
    val name: String,
    val sku: String? = null,
    val barcode: String? = null,
    val brand: String? = null,
    val imageUrl: String? = null,
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val stock: Int = 0,
    val unit: String? = null,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)
