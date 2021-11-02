package com.andrew.stockinfoapp

import androidx.lifecycle.ViewModelProvider
import com.andrew.stockinfoapp.domain.Result
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.presentation.MainViewModel
import com.andrew.stockinfoapp.presentation.StockListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val viewModel = MainViewModel()
        viewModel.searchString.value = "test"
        GlobalScope.launch(Dispatchers.IO) {
        assert(viewModel.getStocksFromQuery() is Result.Success)
    }
    }
}