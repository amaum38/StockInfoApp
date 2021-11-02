package com.andrew.stockinfoapp.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchableStockItems (
    val bestMatches: List<SearchableStock>
)

@Serializable
data class SearchableStock(@SerialName("1. symbol")val symbol: String,
                           @SerialName("2. name")val name: String)