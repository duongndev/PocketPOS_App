package com.duongnd.pocketposapp.feature.store

import com.duongnd.pocketposapp.data.remote.dto.store.BankItem

data class StoreState(
    val storeName: String = "",
    val address: String = "",
    val phone: String = "",
    val description: String = "",
    val bankCode: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val accountHolderName: String = "",
    val banks: List<BankItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val showResultDialog: Boolean = false,
    val resultMessage: String? = null
)
