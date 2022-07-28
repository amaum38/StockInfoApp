package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrew.stockinfoapp.domain.Constants
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.framework.api.Endpoints
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.text.SimpleDateFormat
import java.util.*

class InfoViewModel : ViewModel(),  KoinComponent {
    private val interactors: Interactors = get()
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

    val matching = MediatorLiveData<Stock?>().apply {
        addSource(symbol) {
            viewModelScope.launch {
                postValue(
                    symbol.value?.let { value ->
                        interactors.getStocksBySymbol(value)
                    }
                )
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
     fun loadInfo(symbol: String): NetworkResult = interactors.getStockInfo(symbol)
}