package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(page: Int, limit: Int): Result<List<Category>>
    suspend fun getCategoryById(id: String): Result<Category>
    suspend fun createCategory(category: Category): Result<Category>
    suspend fun updateCategory(id: String, category: Category): Result<Category>
    suspend fun deleteCategory(id: String): Result<Unit>
}
