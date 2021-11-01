package com.andrew.stockinfoapp.interactors

import com.andrew.stockinfoapp.data.StockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetStocksBySymbol(private val stockRepository: StockRepository) {
    suspend operator fun invoke(symbol: String) = withContext(Dispatchers.Default) {
        stockRepository.getStocksBySymbol(symbol)
    }
}