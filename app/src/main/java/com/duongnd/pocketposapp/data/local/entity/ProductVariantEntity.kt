package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_variants")
data class ProductVariantEntity(
    @PrimaryKey(autoGenerate = true)
    val variantId: Int = 0,
    val productId: Int, // Liên kết với ProductEntity
    val sku: String? = null, // Mã quản lý kho (Ví dụ: BUT-THIEN-LONG-XANH)
    val barcode: String? = null,
    val price: Double, // Giá bán của biến thể này
    val costPrice: Double, // Giá vốn
    val stock: Int, // Số lượng tồn kho của riêng biến thể này
    val isActive: Boolean = true
)
