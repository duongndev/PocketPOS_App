package com.duongnd.pocketposapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.dao.ProductDao
import com.duongnd.pocketposapp.data.local.dao.CartDao
import com.duongnd.pocketposapp.data.local.entity.CategoryEntity
import com.duongnd.pocketposapp.data.local.entity.ProductEntity
import com.duongnd.pocketposapp.data.local.entity.CartItemEntity

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
}
