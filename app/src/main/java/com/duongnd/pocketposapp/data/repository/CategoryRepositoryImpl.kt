package com.duongnd.pocketposapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.mapper.toDomain
import com.duongnd.pocketposapp.data.local.mapper.toEntity
import com.duongnd.pocketposapp.data.paging.CategoryPagingSource
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.dto.category.*
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
        parentId: String?,
        sort: String?,
        order: String?
    ): CategoryPage {
        val response = categoryAPI.getCategories(page, limit, search, isActive, parentId, sort, order)
        return response.data.toDomainPage()
    }

    override fun getRemoteCategoriesPager(
        search: String?,
        isActive: Boolean?,
        isChildren: Boolean
    ): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategoryPagingSource(categoryAPI, search, isActive, isChildren)
            }
        ).flow
    }

    override suspend fun getCategoriesChildren(
        parentId: String,
        page: Int,
        limit: Int,
        search: String?,
        isActive: Boolean?
    ): CategoryPage {
        val response = categoryAPI.getCategoriesChildren(page, limit, search, isActive, parentId)
        return response.data.toDomainPage()
    }

    override suspend fun getCategoryTree(): List<CategoryTree> {
        val response = categoryAPI.getCategoryTree()
        return response.data.map { it: CategoryTreeDTO -> it.toDomainModel() }
    }

    override suspend fun getCategoryConstraints(id: String): CategoryConstraints {
        val response = categoryAPI.getCategoryConstraints(id)
        return response.data.toDomainModel() as CategoryConstraints
    }

    override suspend fun createCategory(
        name: String,
        description: String,
        parentId: String?,
        sortOrder: Int?
    ): Category {
        val request = CategoryRequest(name, description, parentId, sortOrder)
        val response = categoryAPI.createCategory(request)
        return response.data.toDomainModel() as Category
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
        return response.data.toDomainModel() as Category
    }

    override suspend fun deleteCategory(id: String) {
        categoryAPI.deleteCategory(id)
    }

    override suspend fun hardDeleteCategory(id: String) {
        categoryAPI.hardDeleteCategory(id)
    }
}
