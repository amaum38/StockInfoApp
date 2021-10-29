package com.andrew.stockinfoapp.data

import com.andrew.stockinfoapp.domain.Stock

class StockRepository(private val dataSource: StockDataSource) {
    suspend fun addStock(stock: Stock) = dataSource.add(stock)
    suspend fun removeStock(stock: Stock) = dataSource.remove(stock)
    suspend fun getStocks() = dataSource.getStocks()
    suspend fun getStocksBySymbol(symbol: String) = dataSource.getStocksBySymbol(symbol)
    suspend fun updateStock(stock: Stock) = dataSource.update(stock)
}