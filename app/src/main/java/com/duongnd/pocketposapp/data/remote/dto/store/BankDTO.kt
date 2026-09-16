package com.duongnd.pocketposapp.data.remote.dto.store

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BankResponse(
    val no_banks: String?,
    val data: List<BankItem>?
)

@JsonClass(generateAdapter = true)
data class BankItem(
    val id: Int? = null,
    val name: String,
    val code: String,
    val bin: String,
    val short_name: String,
    val alias: List<String>? = null,
    val supported: Boolean
)
