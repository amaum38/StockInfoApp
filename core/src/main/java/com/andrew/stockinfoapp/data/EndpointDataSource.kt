package com.andrew.stockinfoapp.data

import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.domain.Stock

interface EndpointDataSource {
    fun getInfo(symbol: String): NetworkResult
    fun searchStocks(query: String): NetworkResult
    fun getPriceData(stock: Stock): List<Float>
}