package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.data.remote.dto.category.CategoryDTO
import com.duongnd.pocketposapp.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getRemoteCategories(
        page: Int = 1,
        limit: Int = 10,
        search: String? = null,
        isActive: Boolean? = null,
        sort: String? = null,
        order: String? = null
    ): CategoryPage

    suspend fun getCategoryTree(): List<CategoryTree>
    suspend fun getCategoryById(id: String): Category?
    suspend fun getCategoryConstraints(id: String): CategoryConstraints
    suspend fun createCategory(name: String, description: String, parentId: String?, sortOrder: Int?): Category
    suspend fun updateCategory(id: String, name: String, description: String, parentId: String?, sortOrder: Int?): Category
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(id: String)
    suspend fun hardDeleteCategory(id: String)
    suspend fun deleteCategoryLocally(category: Category)
}
