package com.andrew.stockinfoapp.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Stock(@SerialName("Symbol") val symbol: String?,
                 @SerialName("Name") val name: String?,
                 @SerialName("Description") val description: String?,
                 @SerialName("PERatio") val peRatio: String?,
                 @SerialName("EPS") val eps: String?,
                 @SerialName("52WeekHigh") val yearHigh: String?,
                 @SerialName("52WeekLow") val yearLow: String?,
                 var dailyData: List<Float> = emptyList(),
                 var lastUpdate: String = SimpleDateFormat(Constants.DATE_FORMAT,
                     Locale.US).format(Date())
)