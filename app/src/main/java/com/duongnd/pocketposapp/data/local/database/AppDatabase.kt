package com.duongnd.pocketposapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.duongnd.pocketposapp.data.local.dao.AttributeDao
import com.duongnd.pocketposapp.data.local.dao.CategoryDao
import com.duongnd.pocketposapp.data.local.dao.ProductDao
import com.duongnd.pocketposapp.data.local.entity.*

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        AttributeEntity::class,
        AttributeValueEntity::class,
        ProductVariantEntity::class,
        VariantAttributeValueCrossRef::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun attributeDao(): AttributeDao
}
