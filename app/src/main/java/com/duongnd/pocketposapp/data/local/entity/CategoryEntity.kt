package com.duongnd.pocketposapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)