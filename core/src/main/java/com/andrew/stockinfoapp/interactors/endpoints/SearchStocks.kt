package com.andrew.stockinfoapp.interactors.endpoints

import com.andrew.stockinfoapp.data.EndpointRepository
import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.domain.Stock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchStocks(private val repository: EndpointRepository) {
    operator fun invoke(query: String) = repository.searchStocks(query)
}