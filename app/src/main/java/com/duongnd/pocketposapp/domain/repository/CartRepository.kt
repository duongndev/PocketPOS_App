package com.duongnd.pocketposapp.domain.repository

import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<ScannedItem>>
    suspend fun addToCart(item: ScannedItem)
    suspend fun updateCount(barcode: String, count: Int)
    suspend fun removeFromCart(barcode: String)
    suspend fun clearCart()
}
