package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Result
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.framework.Interactors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

class InfoViewModel() : ViewModel(),  KoinComponent {
    private val interactors: Interactors by inject()
    var stock = MutableLiveData<Stock>()
    var symbol = MutableLiveData<String>()

    val isInList = MediatorLiveData<Boolean>().apply {
        addSource(symbol) {
            viewModelScope.launch {
                postValue(interactors.getStocks().any {
                    it.symbol == symbol.value
                })
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

    /**
     * Load the stocks information from the api
     */
     fun loadInfo(symbol: String): Result {
        val api: Endpoints by inject()
        val response = api.getOverview(symbol, Constants.API_KEY_2).execute()
        if (response.isSuccessful) {
            val stockResponse = response.body()
            return if (stockResponse != null) {
                stockResponse.lastUpdate = SimpleDateFormat(
                    "dd/M/yyyy hh:mm:ss",
                    Locale.US).format(Date())
                stockResponse.dailyData = emptyList()

                Result.Success().also {
                    it.data = stockResponse
                }
            } else {
                Result.Failure(response.message())
            }
        } else {
            return Result.Failure(response.message())
        }
    }
}