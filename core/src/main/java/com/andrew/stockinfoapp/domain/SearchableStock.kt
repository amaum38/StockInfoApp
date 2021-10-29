package com.andrew.stockinfoapp.domain

import com.google.gson.annotations.SerializedName

data class SearchableStockItems (
    val bestMatches: List<SearchableStock>
)


data class SearchableStock(@SerializedName("1. symbol")val symbol: String,
                           @SerializedName("2. name")val name: String)