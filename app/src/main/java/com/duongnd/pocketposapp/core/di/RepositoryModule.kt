package com.duongnd.pocketposapp.core.di

import com.duongnd.pocketposapp.data.repository.AuthRepositoryImpl
import com.duongnd.pocketposapp.data.repository.CategoryRepositoryImpl
import com.duongnd.pocketposapp.data.repository.ProductRepositoryImpl
import com.duongnd.pocketposapp.data.repository.CartRepositoryImpl
import com.duongnd.pocketposapp.data.repository.StoreRepositoryImpl
import com.duongnd.pocketposapp.domain.repository.AuthRepository
import com.duongnd.pocketposapp.domain.repository.CategoryRepository
import com.duongnd.pocketposapp.domain.repository.ProductRepository
import com.duongnd.pocketposapp.domain.repository.CartRepository
import com.duongnd.pocketposapp.domain.repository.StoreRepository
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

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
    @Binds
    @Singleton
    abstract fun bindStoreRepository(
        storeRepositoryImpl: StoreRepositoryImpl
    ): StoreRepository
}
