package com.andrew.stockinfoapp.framework

import android.app.Application
import com.andrew.stockinfoapp.data.StockRepository
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StockInfoApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val networkModule = module {
            single { provideGson() }
            single { provideOkHttpClient() }
            single { provideRetrofit(get()) }
            single { provideApi(get()) }
        }

        val stockRepository = StockRepository(RoomStockDataSource(this))

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
            androidLogger()
            androidContext(this@StockInfoApp)
            modules(mainModule, networkModule)
        }
    }
}