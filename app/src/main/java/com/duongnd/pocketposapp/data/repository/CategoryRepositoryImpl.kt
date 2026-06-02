package com.duongnd.pocketposapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.duongnd.pocketposapp.core.utils.safeActionCall
import com.duongnd.pocketposapp.core.utils.safeApiCall
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.mapper.toDomain
import com.duongnd.pocketposapp.data.local.mapper.toEntity
import com.duongnd.pocketposapp.data.paging.CategoryPagingSource
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.dto.category.*
import com.duongnd.pocketposapp.data.remote.mapper.*
import com.duongnd.pocketposapp.domain.model.*
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryAPI: CategoryAPI,
    private val moshi: Moshi
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: String): Category? {
        // First try local
        val local = categoryDao.getCategoryById(id)?.toDomain()
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
        page: Int?,
        limit: Int?,
        sortBy: String?,
        sortOrder: String?
    ): CategoryPage {
        val response = categoryAPI.getCategories(page, limit, sortBy, sortOrder)
        return CategoryPage(
            categories = response.data.map { it.toDomainModel() },
            pagination = response.pagination?.toDomainModel() ?: PaginationInfo(
                currentPage = 1,
                totalPages = 1,
                totalItems = response.data.size,
                itemsPerPage = response.data.size,
                hasNextPage = false,
                hasPrevPage = false,
                nextPage = null,
                prevPage = null,
                isFirstPage = true,
                isLastPage = true
            )
        )
    }

    override fun getRemoteCategoriesPager(
        sortBy: String?,
        sortOrder: String?
    ): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategoryPagingSource(categoryAPI, sortBy, sortOrder)
            }
        ).flow
    }

    override suspend fun createCategory(
        name: String,
        description: String?
    ): Category {
        return safeActionCall(
            moshi = moshi,
            apiCall = { categoryAPI.createCategory(CategoryRequest(name, description)) },
            mapper = { it.toDomainModel() }
        ).getOrThrow()
    }

    override suspend fun updateCategory(
        id: String,
        name: String,
        description: String?
    ): Category {
        return safeActionCall(
            moshi = moshi,
            apiCall = { categoryAPI.updateCategory(id, CategoryRequest(name, description)) },
            mapper = { it.toDomainModel() }
        ).getOrThrow()
    }

    override suspend fun deleteCategory(id: String) {
        safeActionCall(
            moshi = moshi,
            apiCall = { categoryAPI.deleteCategory(id) },
            mapper = { it }
        ).getOrThrow()
    }
}
