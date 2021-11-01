package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.framework.StockAdapter
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class StockListViewModel() : ViewModel(), KoinComponent {
    val interactors: Interactors by inject()

    val stocks = MediatorLiveData<List<Stock>>().apply {
        viewModelScope.launch {
            val stocks = interactors.getStocks()
            postValue(stocks)
        }
    }

    /**
     * Check to see if any items need updating, if they do get updated data and update the adapter
     */
    fun checkforUpdates(adapter: StockAdapter) {
        viewModelScope.launch {
            stocks.value?.forEachIndexed { index, stock ->
                //Get a fresh data list if older than 24 hours or empty, otherwise go to the database
                val lastUpdate = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale.US)
                    .parse(stock.lastUpdate)
                val daysOld = (Date().time - lastUpdate.time) / 86400000
                if (stock.dailyData.isEmpty() || daysOld >= 1) {
                    stock.dailyData = getDailyInfo(stock.symbol)
                    stock.lastUpdate = SimpleDateFormat(
                        "dd/M/yyyy hh:mm:ss", Locale.US).format(Date())
                    interactors.updateStock(stock)
                    withContext(Dispatchers.Main) {
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }
    }

    /**
    * Get the daily price info to populate sparkline
    */
    private suspend fun getDailyInfo(symbol: String): List<Float> {
        var reversed: List<Float> = emptyList()
        withContext(Dispatchers.Default) {
            var data: MutableList<Float> = mutableListOf()
            val api: Endpoints by inject()
            val call = api.getPriceData(symbol, "15min", Constants.API_KEY_2)
            val response = call.execute()
            if (response.isSuccessful) {
                if (response.body() != null && response.body()!!.isJsonObject) {
                    val responseObject: JsonObject = response.body() as JsonObject
                    if (responseObject.has("Time Series (15min)")) {
                        val timeSeries = responseObject.get("Time Series (15min)") as JsonObject
                        val keySet = timeSeries.keySet()

                        for (key in keySet) {
                            val series = timeSeries.get(key) as JsonObject;
                            data.add(series.get("4. close").asFloat)
                        }
                    }
                }

                reversed = data.reversed()
            }
        }

        return reversed
    }
}