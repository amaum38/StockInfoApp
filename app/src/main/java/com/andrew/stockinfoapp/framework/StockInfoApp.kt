package com.andrew.stockinfoapp.framework

import android.app.Application
import com.andrew.stockinfoapp.data.EndpointRepository
import com.andrew.stockinfoapp.data.StockRepository
import com.andrew.stockinfoapp.framework.api.provideApi
import com.andrew.stockinfoapp.framework.api.provideJson
import com.andrew.stockinfoapp.framework.api.provideOkHttpClient
import com.andrew.stockinfoapp.framework.api.provideRetrofit
import com.andrew.stockinfoapp.interactors.*
import com.andrew.stockinfoapp.interactors.endpoints.GetPriceInfo
import com.andrew.stockinfoapp.interactors.endpoints.GetStockInfo
import com.andrew.stockinfoapp.interactors.endpoints.SearchStocks
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
            val k =
            single {
                val endpointRepo = EndpointRepository(EndpointDataSource(get()))
                Interactors(
                    AddStock(stockRepository),
                    RemoveStock(stockRepository),
                    GetStocks(stockRepository),
                    GetStocksBySymbol(stockRepository),
                    UpdateStock(stockRepository),
                    GetStockInfo(endpointRepo),
                    GetPriceInfo(endpointRepo),
                    SearchStocks(endpointRepo)
                )
            }
        }

        startKoin{
            androidLogger(Level.NONE)
            androidContext(this@StockInfoApp)
            modules(mainModule, networkModule)
        }
    }
}