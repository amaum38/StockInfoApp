package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.domain.Stock

class UpdateStock(private val stockRepository: StockRepository) {
    suspend operator fun invoke(stock: Stock) = stockRepository.updateStock(stock)
}