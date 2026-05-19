package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants")
data class ProductVariantEntity(
    @PrimaryKey(autoGenerate = true)
    val variantId: Int = 0,
    val productId: Int,
    val name: String,
    val sku: String? = null,
    val barcode: String? = null,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val reserved: Int = 0,
    val unit: String = "cái",
    val conversionRate: Double = 1.0,
    val imageUri: String? = null,
    val isDefault: Boolean = false,
    val lowStockThreshold: Int = 5,
    val isActive: Boolean = true
)
