package com.duongnd.pocketposapp.core.utils

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
    return formatter.format(price)
}

fun formatPriceToDouble(price: String): Double {
    val cleanString = price.replace(Regex("[^\\d]"), "")
    return cleanString.toDoubleOrNull() ?: 0.0
}

fun formatInputPrice(input: String): String {
    val cleanString = input.replace(Regex("[^\\d]"), "")
    if (cleanString.isEmpty()) return ""
    val parsed = cleanString.toDoubleOrNull() ?: return input
    return formatPrice(parsed)
}