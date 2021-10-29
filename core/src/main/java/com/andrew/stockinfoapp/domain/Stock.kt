package com.andrew.stockinfoapp.domain

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Stock(@SerializedName("Symbol") val symbol: String,
                 @SerializedName("Name") val name: String,
                 @SerializedName("Description") val description: String,
                 @SerializedName("PERatio") val peRatio: String,
                 @SerializedName("EPS") val eps: String,
                 @SerializedName("52WeekHigh") val yearHigh: String,
                 @SerializedName("52WeekLow") val yearLow: String,
                 var dailyData: List<Float> = emptyList(),
                 var lastUpdate: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss",
                     Locale.US).format(Date())
)