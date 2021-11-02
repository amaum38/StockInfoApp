package com.andrew.stockinfoapp.interactors

import com.andrew.stockinfoapp.data.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetStocks(private val stockRepository: StockRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        stockRepository.getStocks()
    }
}