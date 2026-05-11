package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val slug: String,
    val description: String? = null,
    val parentId: String? = null,
    val sortOrder: Int? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)