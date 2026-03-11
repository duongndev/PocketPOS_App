package com.duongnd.pocketposapp.core.utils

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Long): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return formatter.format(price)
}