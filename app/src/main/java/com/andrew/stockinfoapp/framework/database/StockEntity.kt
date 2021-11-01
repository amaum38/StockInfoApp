package com.andrew.stockinfoapp.framework.database

import androidx.room.*


@Entity(tableName = "stock")
data class StockEntity(
    @PrimaryKey val symbol: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "Description") val description: String,
    @ColumnInfo(name = "PERatio") val peRatio: String,
    @ColumnInfo(name = "EPS") val eps: String,
    @ColumnInfo(name = "52WeekHigh") val yearHigh: String,
    @ColumnInfo(name = "52WeekLow") val yearLow: String,
    @ColumnInfo(name = "data") val data: List<Float>,
    @ColumnInfo(name = "lastUpdate") val lastUpdate: String
)