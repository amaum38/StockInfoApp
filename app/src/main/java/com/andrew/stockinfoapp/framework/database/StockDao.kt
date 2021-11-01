package com.andrew.stockinfoapp.framework.database

import androidx.room.*

@Dao
interface StockDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStock(stock: StockEntity)

    @Query("SELECT * FROM stock")
    fun getStocks(): List<StockEntity>

    @Query("SELECT * FROM stock WHERE symbol Like :symbol")
    fun getStocksBySymbol(symbol: String):List<StockEntity>

    @Delete
    fun removeStock(stock: StockEntity)

    @Update
    fun updateStock(stock: StockEntity)
}