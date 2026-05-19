package com.duongnd.pocketposapp.data.remote.dto.product

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VariantAttributeDTO(
    val name: String,
    val value: String
)
