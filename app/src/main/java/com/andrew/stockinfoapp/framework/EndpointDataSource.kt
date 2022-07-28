package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.data.EndpointDataSource
import com.andrew.stockinfoapp.domain.Constants
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.api.Endpoints
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import org.koin.core.component.get
import java.text.SimpleDateFormat
import java.util.*

class EndpointDataSource(private val api: Endpoints) : EndpointDataSource {
    override fun getInfo(symbol: String): NetworkResult {
        val response = api.getOverview(symbol).execute()
        return if (response.isSuccessful) {
            val stockResponse = response.body()
            if (stockResponse != null) {
                stockResponse.lastUpdate = SimpleDateFormat(
                    Constants.DATE_FORMAT,
                    Locale.US).format(Date())
                stockResponse.dailyData = emptyList()
                NetworkResult.Success(stockResponse)
            } else {
                NetworkResult.Failure(response.message())
            }
        } else {
            NetworkResult.Failure(response.message())
        }
    }

    override fun searchStocks(query: String): NetworkResult {
        val response = api.getSymbols(query).execute()
        return if (response.isSuccessful) {
            val stocks = response.body()?.bestMatches.orEmpty()
            if (stocks.isNotEmpty()) {
                NetworkResult.Success(stocks)
            } else {
                NetworkResult.Failure("No results found")
            }
        } else {
            NetworkResult.Failure(response.message())
        }
    }

    override fun getPriceData(stock: Stock): List<Float> {
        val response = api.getPriceData(stock.symbol, "15min").execute()
        return if (response.isSuccessful && response.body() != null && response.body()!! is JsonObject) {
            val responseObject: JsonObject = response.body() as JsonObject
            if (responseObject.containsKey("Time Series (15min)")) {
                val timeSeries = responseObject["Time Series (15min)"] as JsonObject
                timeSeries.keys.mapNotNull { key ->
                    ((timeSeries[key] as JsonObject)["4. close"] as JsonPrimitive).floatOrNull
                }.reversed()
            } else emptyList()
        } else emptyList()
    }
}