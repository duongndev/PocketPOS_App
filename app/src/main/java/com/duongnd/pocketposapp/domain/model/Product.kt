package com.duongnd.pocketposapp.domain.model

data class Product(
    val id: String = "",
    val name: String,
    val slug: String = "",
    val categoryId: String,
    val categoryName: String = "",
    val brand: String = "",
    val description: String? = null,
    val imageUri: String? = null,
    val hasVariants: Boolean = false,
    val options: List<ProductOption> = emptyList(),
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true,
    val deletedAt: String? = null,
    val variants: List<ProductVariant> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class ProductOption(
    val name: String,
    val values: List<String>
)

data class ProductVariant(
    val id: String = "",
    val name: String = "", // Derived or optional
    val productId: String,
    val sku: String? = null,
    val barcode: String? = null,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val reserved: Int = 0,
    val unit: String = "cái",
    val conversionRate: Double = 1.0,
    val attributes: List<VariantAttribute> = emptyList(),
    val imageUri: String? = null,
    val isDefault: Boolean = false,
    val lowStockThreshold: Int = 5,
    val isActive: Boolean = true
)

data class VariantAttribute(
    val name: String, // Ví dụ: size
    val value: String         // Ví dụ: S
)
