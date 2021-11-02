package com.andrew.stockinfoapp.interactors

import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.domain.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddStock(private val stockRepository: StockRepository) {
    suspend operator fun invoke(stock: Stock) = withContext(Dispatchers.IO) {
        stockRepository.addStock(stock)
    }
}