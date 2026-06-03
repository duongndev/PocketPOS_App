package com.duongnd.pocketposapp.feature.store

data class StoreState(
    val storeName: String = "",
    val address: String = "",
    val phone: String = "",
    val description: String = "",
    val bankName: String = "",
    val bankAccountNumber: String = "",
    val bankAccountName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val showResultDialog: Boolean = false,
    val resultMessage: String? = null
)
