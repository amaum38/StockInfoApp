package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.interactors.*

data class Interactors(
    val addStock: AddStock,
    val removeStock: RemoveStock,
    val getStocks: GetStocks,
    val getStocksBySymbol: GetStocksBySymbol,
    val updateStock: UpdateStock
)