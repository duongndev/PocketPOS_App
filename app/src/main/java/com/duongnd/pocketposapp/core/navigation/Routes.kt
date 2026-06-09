package com.duongnd.pocketposapp.core.navigation


object Routes {

    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"

    const val SCANNER = "scanner"
    const val CHECKOUT = "checkout"
    const val PAYMENT_QR = "payment_qr/{orderId}/{totalPrice}/{qrUrl}"
    fun paymentQr(orderId: String, totalPrice: Double, qrUrl: String) = 
        "payment_qr/$orderId/$totalPrice/${java.net.URLEncoder.encode(qrUrl, "UTF-8")}"

    const val PAYMENT_SUCCESS = "payment_success"
    fun paymentSuccess() = PAYMENT_SUCCESS

    const val PRODUCTS = "products"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val ADD_PRODUCT = "add_product"
    const val EDIT_PRODUCT = "edit_product/{productId}"
    const val PRODUCT_VARIANTS = "product_variants"

    const val CATEGORIES = "categories"
    const val ADD_CATEGORY = "add_category"

    const val ORDERS = "orders"
    const val ORDER_DETAIL = "order_detail/{orderId}"

    const val STATISTICS = "statistics"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val CHANGE_PASSWORD = "change_password"
    const val STORE_INFO = "store_info?from={from}"
    fun storeInfo(from: String? = null) = if (from != null) "store_info?from=$from" else "store_info"
    const val PRINTER_CONFIG = "printer_config"
}