package com.andrew.stockinfoapp.data

import com.andrew.stockinfoapp.domain.Stock

class EndpointRepository(private val dataSource: EndpointDataSource) {
    fun getInfo(symbol: String) = dataSource.getInfo(symbol)
    fun searchStocks(query: String) = dataSource.searchStocks(query)
    fun getPriceData(stock: Stock) = dataSource.getPriceData(stock)
}