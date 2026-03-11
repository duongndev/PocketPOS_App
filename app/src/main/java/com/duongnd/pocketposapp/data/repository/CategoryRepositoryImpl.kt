package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.core.utils.safeApiCall
import com.duongnd.pocketposapp.data.remote.api.CategoryAPI
import com.duongnd.pocketposapp.data.remote.mapper.toDTO
import com.duongnd.pocketposapp.data.remote.mapper.toDomain
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryAPI
) : CategoryRepository {

    override suspend fun getCategories(page: Int, limit: Int): Result<List<Category>> {
        return safeApiCall(
            apiCall = { api.getCategories(page, limit) },
            mapper = { response -> response.categories.map { it.toDomain() } }
        )
    }

    override suspend fun getCategoryById(id: String): Result<Category> {
        return safeApiCall(
            apiCall = { api.getCategoryById(id) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun createCategory(category: Category): Result<Category> {
        return safeApiCall(
            apiCall = { api.createCategory(category.toDTO()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun updateCategory(id: String, category: Category): Result<Category> {
        return safeApiCall(
            apiCall = { api.updateCategory(id, category.toDTO()) },
            mapper = { it.toDomain() }
        )
    }

    override suspend fun deleteCategory(id: String): Result<Unit> {
        return safeApiCall(
            apiCall = { api.deleteCategory(id) },
            mapper = { Unit }
        )
    }
}
