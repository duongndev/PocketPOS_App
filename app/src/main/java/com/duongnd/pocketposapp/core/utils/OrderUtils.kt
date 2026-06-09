package com.duongnd.pocketposapp.core.utils

import androidx.compose.ui.graphics.Color

enum class OrderStatus(val label: String, val color: Color) {
    PENDING("Chờ thanh toán", Color(0xFFFFA000)),
    COMPLETED("Hoàn thành", Color(0xFF4CAF50)),
    CANCELLED("Đã hủy", Color(0xFFF44336));

    companion object {
        fun fromString(status: String): OrderStatus {
            return when (status.lowercase()) {
                "pending" -> PENDING
                "completed" -> COMPLETED
                "cancelled" -> CANCELLED
                else -> COMPLETED
            }
        }
    }
}

fun formatPaymentMethod(method: String): String {
    return when (method.uppercase()) {
        "CASH" -> "Tiền mặt"
        "BANK_TRANSFER", "QR" -> "Chuyển khoản"
        else -> method
    }
}

fun formatPaymentStatus(status: String): String {
    return when (status.lowercase()) {
        "paid" -> "Đã thanh toán"
        "unpaid" -> "Chưa thanh toán"
        "partially_paid" -> "Thanh toán một phần"
        "refunded" -> "Đã hoàn tiền"
        else -> status
    }
}
