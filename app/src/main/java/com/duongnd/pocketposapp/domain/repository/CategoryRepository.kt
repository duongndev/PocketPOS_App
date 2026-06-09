package com.duongnd.pocketposapp.domain.repository

import androidx.paging.PagingData
import com.duongnd.pocketposapp.data.remote.dto.category.CategoryDTO
import com.duongnd.pocketposapp.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun getRemoteCategories(
        page: Int? = 1,
        limit: Int? = 10,
        sortBy: String? = null,
        sortOrder: String? = null
    ): CategoryPage

    fun getRemoteCategoriesPager(
        sortBy: String? = null,
        sortOrder: String? = null
    ): Flow<PagingData<Category>>

    suspend fun getCategoryById(id: String): Category?
    suspend fun createCategory(name: String, description: String?): Category
    suspend fun updateCategory(id: String, name: String, description: String?): Category
    suspend fun upsertCategory(category: Category)
    suspend fun deleteCategory(id: String)
    suspend fun deleteCategoryLocally(category: Category)
}
