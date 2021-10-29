package com.andrew.stockinfoapp.data

import com.andrew.stockinfoapp.domain.Stock

interface StockDataSource {
    suspend fun add(stock: Stock)
    suspend fun remove(stock: Stock)
    suspend fun getStocks(): List<Stock>
    suspend fun getStocksBySymbol(symbol: String): List<Stock>
    suspend fun update(stock: Stock)
}