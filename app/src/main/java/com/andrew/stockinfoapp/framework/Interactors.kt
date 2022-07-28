package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.interactors.*
import com.andrew.stockinfoapp.interactors.endpoints.GetPriceInfo
import com.andrew.stockinfoapp.interactors.endpoints.GetStockInfo
import com.andrew.stockinfoapp.interactors.endpoints.SearchStocks

data class Interactors(
    val addStock: AddStock,
    val removeStock: RemoveStock,
    val getStocks: GetStocks,
    val getStocksBySymbol: GetStocksBySymbol,
    val updateStock: UpdateStock,
    val getStockInfo: GetStockInfo,
    val getPriceInfo: GetPriceInfo,
    val searchStocks: SearchStocks
)