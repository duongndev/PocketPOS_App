package com.duongnd.pocketposapp.data.remote.dto.product

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VariantAttributeDTO (
    val size: String?,
    val color: String?,
)