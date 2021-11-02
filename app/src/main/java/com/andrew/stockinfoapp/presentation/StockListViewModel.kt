package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.framework.StockAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class StockListViewModel() : ViewModel(), KoinComponent {
    private val interactors: Interactors by inject()

    val stocks = MutableLiveData<List<Stock>>().apply {
        viewModelScope.launch {
            val stocks = interactors.getStocks()
            postValue(stocks)
        }
    }

    /**
     * Check to see if any items need updating, if they do get updated data and update the adapter
     */
    fun checkforUpdates(adapter: StockAdapter) {
        viewModelScope.launch(Dispatchers.IO) {
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
    private fun getDailyInfo(symbol: String): List<Float> {
        var reversed: List<Float> = emptyList()
            val data: MutableList<Float> = mutableListOf()
            val api: Endpoints by inject()
            val call = api.getPriceData(symbol, "15min", Constants.API_KEY_2)
            val response = call.execute()
            if (response.isSuccessful) {
                if (response.body() != null && response.body()!! is JsonObject) {
                    val responseObject: JsonObject = response.body() as JsonObject
                    if (responseObject.containsKey("Time Series (15min)")) {
                        val timeSeries = responseObject["Time Series (15min)"] as JsonObject
                        for (key in timeSeries.keys) {
                            val series = timeSeries[key] as JsonObject;
                            (series["4. close"] as JsonPrimitive).floatOrNull?.let { data.add(it) }
                        }
                    }
                }

                reversed = data.reversed()
            }

        return reversed
    }
}