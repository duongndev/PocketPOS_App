package com.duongnd.pocketposapp.data.repository

import com.duongnd.pocketposapp.data.local.dao.CartDao
import com.duongnd.pocketposapp.data.local.entity.CartItemEntity
import com.duongnd.pocketposapp.domain.repository.CartRepository
import com.duongnd.pocketposapp.feature.scanner.ScannedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<ScannedItem>> {
        return cartDao.getAllCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(item: ScannedItem) {
        val existing = cartDao.getCartItemById(item.productId)
        if (existing != null) {
            cartDao.updateCartItem(existing.copy(count = existing.count + item.count))
        } else {
            cartDao.insertOrUpdate(item.toEntity())
        }
    }

    override suspend fun updateCount(barcode: String, count: Int) {
        val items = cartDao.getAllCartItems().first()
        val item = items.find { it.barcode == barcode }
        if (item != null) {
            if (count > 0) {
                cartDao.updateCartItem(item.copy(count = count))
            } else {
                cartDao.deleteCartItem(item.productId)
            }
        }
    }

    override suspend fun removeFromCart(barcode: String) {
        val items = cartDao.getAllCartItems().first()
        val item = items.find { it.barcode == barcode }
        if (item != null) {
            cartDao.deleteCartItem(item.productId)
        }
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}

fun CartItemEntity.toDomain() = ScannedItem(
    productId = productId,
    barcode = barcode,
    name = name,
    price = price,
    count = count
)

fun ScannedItem.toEntity() = CartItemEntity(
    productId = productId,
    barcode = barcode,
    name = name,
    price = price,
    count = count
)
