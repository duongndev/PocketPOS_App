package com.duongnd.pocketposapp.core.di

import android.content.Context
import androidx.room.Room
import com.duongnd.pocketposapp.data.local.dao.AttributeDao
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.dao.ProductDao
import com.duongnd.pocketposapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pocket_pos_db"
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideAttributeDao(database: AppDatabase): AttributeDao {
        return database.attributeDao()
    }
}
