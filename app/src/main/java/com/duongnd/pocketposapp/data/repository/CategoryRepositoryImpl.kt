package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.mapper.toDomain
import com.duongnd.pocketposapp.data.local.mapper.toEntity
import com.duongnd.pocketposapp.domain.model.Category
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }

    override suspend fun upsertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }
}
