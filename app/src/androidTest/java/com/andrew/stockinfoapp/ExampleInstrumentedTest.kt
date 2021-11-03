package com.andrew.stockinfoapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.andrew.stockinfoapp.domain.Result
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.presentation.InfoViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.component.inject
import org.koin.test.KoinTest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentedTest : KoinTest {
    var stock = Stock(description = "",
        name = "",
        eps = "",
        peRatio = "",
        yearLow = "",
        yearHigh = "",
        symbol = "")

    @Test
    fun getItem() {
        val interactors: Interactors by inject()
        runBlocking {
            interactors.addStock(stock)
            val list = interactors.getStocks()
            assert(list.contains(stock))
            interactors.removeStock(stock)

        }
    }

    @Test
    fun getBySymbol() {
        val interactors: Interactors by inject()
        runBlocking {
            interactors.addStock(stock)

            val matching = interactors.getStocksBySymbol("")
            assert(matching.contains(stock))
            val notMatching = interactors.getStocksBySymbol("test")
            assert(!notMatching.any { it.symbol == "test" })

            interactors.removeStock(stock)
        }
    }

    @Test
    fun addAndRemoveItem() {
        val interactors: Interactors by inject()
        runBlocking {
            interactors.addStock(stock)
            var list = interactors.getStocks()
            assert(list.contains(stock))
            interactors.removeStock(stock)
            list = interactors.getStocks()
            assert(!list.contains(stock))
        }
    }

    @Test
    fun updateItem() {
        val interactors: Interactors by inject()
        runBlocking {
            interactors.addStock(stock)

            interactors.updateStock(Stock(description = "",
                name = "updatedName",
                eps = "",
                peRatio = "",
                yearLow = "",
                yearHigh = "",
                symbol = ""))
            val updated = interactors.getStocksBySymbol("")
            assert(updated[0].name == "updatedName")

            interactors.removeStock(stock)
        }
    }

    /*@Test
    fun getInfo() {
        val viewModel = InfoViewModel()
        runBlocking<Unit> {
            val result = viewModel.loadInfo("NVDA")
            assert(result is Result.Success)
            assert(((result as Result.Success).data as Stock).symbol == "NVDA")
        }
    }*/
}