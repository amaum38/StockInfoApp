package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.framework.Interactors
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class InfoViewModel() : ViewModel(),  KoinComponent {
    val interactors: Interactors by inject()
    var stock = MutableLiveData<Stock>()
    var symbol = MutableLiveData<String>()

    val isInList = MediatorLiveData<Boolean>().apply {
        addSource(symbol) {
            viewModelScope.launch {
                postValue(isInList())
            }
        }
    }

    val matching = MediatorLiveData<List<Stock>>().apply {
        addSource(symbol) {
            viewModelScope.launch {
                postValue(symbol.value?.let { it1 -> interactors.getStocksBySymbol(it1) })
            }
        }
    }

    fun addStock(stock: Stock) {
        viewModelScope.launch {
            interactors.addStock(stock)
        }
    }

    fun removeStock(stock: Stock) {
        viewModelScope.launch {
            interactors.removeStock(stock)
        }
    }

    suspend fun isInList() = interactors.getStocks().any { it.symbol == symbol.value }

    /**
     * Load the stocks information from the api
     */
    fun loadInfo(symbol: String): String {
        var returnCode: String = ""
        viewModelScope.launch {
            val api: Endpoints by inject()
            val call = api.getOverview(symbol, Constants.API_KEY_2)
            call.enqueue(object : Callback<Stock> {
                override fun onResponse(call: Call<Stock>, response: Response<Stock>) {
                    if (response.isSuccessful) {
                        val stockResponse = response.body()
                        stockResponse?.lastUpdate = SimpleDateFormat(
                            "dd/M/yyyy hh:mm:ss",
                            Locale.US
                        ).format(Date())
                        stockResponse?.dailyData = emptyList()
                        stock.value = stockResponse!!
                    } else {
                        returnCode = response.code().toString()
                    }
                }

                override fun onFailure(call: Call<Stock>, t: Throwable) {
                    returnCode = t.message.toString()
                }
            })
        }

        return returnCode
    }
}