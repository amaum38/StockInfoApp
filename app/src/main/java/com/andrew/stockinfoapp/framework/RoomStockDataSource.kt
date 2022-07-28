package com.andrew.stockinfoapp.framework

import android.content.Context
import com.andrew.stockinfoapp.data.StockDataSource
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.database.AppDatabase
import com.andrew.stockinfoapp.framework.database.StockEntity

class RoomStockDataSource(context: Context) : StockDataSource {
    private val dao = AppDatabase.getInstance(context).stockDao()

    override suspend fun add(stock: Stock) {
        dao.addStock(
            StockEntity(
                symbol = stock.symbol,
                name = stock.name,
                description = stock.description,
                peRatio = stock.peRatio,
                eps = stock.eps,
                yearHigh = stock.yearHigh,
                yearLow = stock.yearLow,
                data = stock.dailyData,
                lastUpdate = stock.lastUpdate
            )
        )
    }

    override suspend fun remove(stock: Stock) {
        dao.removeStock(
            StockEntity(
                symbol = stock.symbol,
                name = stock.name,
                description = stock.description,
                peRatio = stock.peRatio,
                eps = stock.eps,
                yearHigh = stock.yearHigh,
                yearLow = stock.yearLow,
                data = stock.dailyData,
                lastUpdate = stock.lastUpdate
            )
        )
    }

    override suspend fun getStocks(): List<Stock> = dao.getStocks().map {
        Stock(
            it.symbol,
            it.name,
            it.description,
            it.peRatio,
            it.eps,
            it.yearHigh,
            it.yearLow,
            it.data,
            it.lastUpdate
        )
    }

    override suspend fun getStocksBySymbol(symbol: String): Stock? =
        dao.getStocksBySymbol(symbol).map {
            Stock(
                it.symbol,
                it.name,
                it.description,
                it.peRatio,
                it.eps,
                it.yearHigh,
                it.yearLow,
                it.data,
                it.lastUpdate
            )
        }.firstOrNull()

    override suspend fun update(stock: Stock) {
        dao.updateStock(
            StockEntity(
                symbol = stock.symbol,
                name = stock.name,
                description = stock.description,
                peRatio = stock.peRatio,
                eps = stock.eps,
                yearHigh = stock.yearHigh,
                yearLow = stock.yearLow,
                data = stock.dailyData,
                lastUpdate = stock.lastUpdate
            )
        )
    }
}