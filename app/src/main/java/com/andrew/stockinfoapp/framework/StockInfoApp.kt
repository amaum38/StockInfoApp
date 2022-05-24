package com.andrew.stockinfoapp.framework

import android.app.Application
import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.framework.api.provideApi
import com.andrew.stockinfoapp.framework.api.provideJson
import com.andrew.stockinfoapp.framework.api.provideOkHttpClient
import com.andrew.stockinfoapp.framework.api.provideRetrofit
import com.andrew.stockinfoapp.interactors.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class StockInfoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val stockRepository = StockRepository(RoomStockDataSource(this))

        val networkModule = module {
            single { provideJson() }
            single { provideOkHttpClient() }
            single { provideRetrofit(get()) }
            single { provideApi(get()) }
        }

        val mainModule = module {
            single { Interactors(
                AddStock(stockRepository),
                RemoveStock(stockRepository),
                GetStocks(stockRepository),
                GetStocksBySymbol(stockRepository),
                UpdateStock(stockRepository)
            ) }
        }

        startKoin{
            androidLogger(Level.NONE)
            androidContext(this@StockInfoApp)
            modules(mainModule, networkModule)
        }
    }
}