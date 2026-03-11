package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Int): Category?
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(category: Category)
}
