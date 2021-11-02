package com.andrew.stockinfoapp

import com.andrew.stockinfoapp.data.StockDataSource
import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.interactors.*
import kotlinx.coroutines.runBlocking
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val testStock = Stock(
        "test", "", "", "", "",
        "", ""
    )

    val test: StockDataSource = object: StockDataSource {
        override suspend fun add(stock: Stock) {
            assert(stock == testStock)
        }

        override suspend fun remove(stock: Stock) {
            assert(stock == testStock)
        }

        override suspend fun getStocks(): List<Stock> {
            TODO("Not yet implemented")
        }

        override suspend fun getStocksBySymbol(symbol: String): List<Stock> {
            TODO("Not yet implemented")
        }

        override suspend fun update(stock: Stock) {
            assert(stock == testStock)
        }

    }

    val repo = StockRepository(test)
    val interactors = Interactors(
        AddStock(repo),
        RemoveStock(repo),
        GetStocks(repo),
        GetStocksBySymbol(repo),
        UpdateStock(repo))

    @Test
    fun addStock() {
        runBlocking<Unit> {
            interactors.addStock(testStock)
        }
    }

    @Test
    fun removeStock() {
        runBlocking<Unit> {
            interactors.removeStock(testStock)
        }
    }

    @Test
    fun updatedStock() {
        runBlocking<Unit> {
            interactors.updateStock(testStock)
        }
    }
}