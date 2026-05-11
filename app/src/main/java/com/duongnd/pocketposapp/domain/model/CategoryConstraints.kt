package com.duongnd.pocketposapp.domain.model

data class CategoryConstraints(
    val category: CategoryMinimal,
    val constraints: ConstraintsInfo,
    val warnings: CategoryWarnings
)

data class CategoryMinimal(
    val id: String,
    val name: String,
    val slug: String,
    val isActive: Boolean
)

data class ConstraintsInfo(
    val childrenCount: Int,
    val productsCount: Int,
    val canDelete: Boolean,
    val hasActiveChildren: Boolean,
    val hasActiveProducts: Boolean
)

data class CategoryWarnings(
    val hasChildren: Boolean,
    val hasProducts: Boolean,
    val canSoftDelete: Boolean,
    val canHardDelete: Boolean
)
