package com.duongnd.pocketposapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val barcode: String,
    val category: Category,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
