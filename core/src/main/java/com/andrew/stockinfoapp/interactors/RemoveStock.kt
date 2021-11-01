package com.andrew.stockinfoapp.interactors

import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.domain.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RemoveStock(private val stockRepository: StockRepository) {
    suspend operator fun invoke(stock: Stock) = withContext(Dispatchers.Default) {
        stockRepository.removeStock(stock)
    }
}