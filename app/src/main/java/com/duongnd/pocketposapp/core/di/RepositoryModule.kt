package com.duongnd.pocketposapp.core.di

import com.duongnd.pocketposapp.data.repository.CategoryRepositoryImpl
import com.duongnd.pocketposapp.data.repository.ProductRepositoryImpl
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
}
