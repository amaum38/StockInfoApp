package com.andrew.stockinfoapp.interactors

import com.andrew.stockinfoapp.data.StockRepository

class GetStocks(private val stockRepository: StockRepository) {
    suspend operator fun invoke() = stockRepository.getStocks()
}