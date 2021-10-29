package com.andrew.stockinfoapp.framework

data class Interactors(
    val addStock: AddStock,
    val removeStock: RemoveStock,
    val getStocks: GetStocks,
    val getStocksBySymbol: GetStocksBySymbol,
    val updateStock: UpdateStock
)