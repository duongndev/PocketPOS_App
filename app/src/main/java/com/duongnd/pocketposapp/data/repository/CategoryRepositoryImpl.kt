package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.mapper.toDomain
import com.duongnd.pocketposapp.data.local.mapper.toEntity
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.dto.category.CategoryRequest
import com.duongnd.pocketposapp.data.remote.mapper.*
import com.duongnd.pocketposapp.domain.model.*
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryAPI: CategoryAPI
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: String): Category? {
        // First try local
        val local = categoryDao.getCategoryById(id.toIntOrNull() ?: 0)?.toDomain()
        if (local != null) return local

        // Then try remote
        return try {
            categoryAPI.getCategoryById(id).data.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun upsertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategoryLocally(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    override suspend fun getRemoteCategories(
        page: Int,
        limit: Int,
        search: String?,
        isActive: Boolean?,
        sort: String?,
        order: String?
    ): CategoryPage {
        val response = categoryAPI.getCategories(page, limit, search, isActive, sort, order)
        return response.data.toDomainPage()
    }

    override suspend fun getCategoryTree(): List<CategoryTree> {
        val response = categoryAPI.getCategoryTree()
        return response.data.map { it.toDomainModel() }
    }

    override suspend fun getCategoryConstraints(id: String): CategoryConstraints {
        val response = categoryAPI.getCategoryConstraints(id)
        return response.data.toDomainModel()
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        parentId: String?,
        sortOrder: Int?
    ): Category {
        val request = CategoryRequest(name, description, parentId, sortOrder)
        val response = categoryAPI.createCategory(request)
        return response.data.toDomainModel()
    }

    override suspend fun updateCategory(
        id: String,
        name: String,
        description: String,
        parentId: String?,
        sortOrder: Int?
    ): Category {
        val request = CategoryRequest(name, description, parentId, sortOrder)
        val response = categoryAPI.updateCategory(id, request)
        return response.data.toDomainModel()
    }

    override suspend fun deleteCategory(id: String) {
        categoryAPI.deleteCategory(id)
    }

    override suspend fun hardDeleteCategory(id: String) {
        categoryAPI.hardDeleteCategory(id)
    }
}
