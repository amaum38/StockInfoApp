package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.data.StockRepository

class GetStocksBySymbol(private val stockRepository: StockRepository) {
    suspend operator fun invoke(symbol: String) = stockRepository.getStocksBySymbol(symbol)
}