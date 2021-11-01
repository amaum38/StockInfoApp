package com.andrew.stockinfoapp.interactors.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [StockEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(FloatListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        const val DATABASE_NAME = "stockinfoapp.db"

        private var instance: AppDatabase? = null

        private fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()


        fun getInstance(context: Context): AppDatabase =
            (instance ?: create(context)).also { instance = it }
    }

    abstract fun stockDao(): StockDao
}