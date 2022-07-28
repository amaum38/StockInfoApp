package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Constants
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.framework.StockAdapter
import com.andrew.stockinfoapp.framework.api.Endpoints
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.text.SimpleDateFormat
import java.util.*

class StockListViewModel : ViewModel(), KoinComponent {
    private val interactors: Interactors = get()
    val stocks = MutableLiveData<List<Stock>>()

    suspend fun loadStockList() {
        stocks.postValue(interactors.getStocks())
    }

    /**
     * Check to see if any items need updating, if they do get updated data and update the adapter
     */
    fun checkForUpdates(adapter: StockAdapter) {
        viewModelScope.launch(Dispatchers.Main) {
            val indexesToUpdate = mutableListOf<Deferred<Int>>()
            stocks.value?.forEachIndexed { index, stock ->
                //Get a fresh data list if older than 24 hours or empty, otherwise go to the database
                val lastUpdate = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US)
                    .parse(stock.lastUpdate)
                val daysOld = (Date().time - (lastUpdate?.time ?: Date().time)) / 86400000
                if (stock.dailyData.isEmpty() || daysOld >= 1) {
                    indexesToUpdate.add(async(Dispatchers.IO) {
                        //getDailyInfo(stock, index)
                        val priceInfo = interactors.getPriceInfo(stock)
                        if (priceInfo.isNotEmpty()) {
                            stock.dailyData = priceInfo
                            stock.lastUpdate = SimpleDateFormat(Constants.DATE_FORMAT, Locale.US).format(Date())
                            interactors.updateStock(stock)
                        }

                        index
                    })
                }
            }

            //run the update code in parallel using async and await all responses before updating the adapter
            indexesToUpdate.awaitAll()
            indexesToUpdate.forEach {
                adapter.notifyItemChanged(it.getCompleted())
            }
        }
    }
}