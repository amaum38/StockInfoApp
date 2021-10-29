package com.andrew.stockinfoapp.framework.database

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*


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