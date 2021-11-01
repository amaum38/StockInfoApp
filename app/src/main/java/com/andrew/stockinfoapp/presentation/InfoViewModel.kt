package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InfoViewModel() : ViewModel(),  KoinComponent {
    val interactors: Interactors by inject()
    var stock = MutableLiveData<Stock>()
    var symbol = MutableLiveData<String>()

    val isInList = MediatorLiveData<Boolean>().apply {
        addSource(stock) {
            GlobalScope.launch {
                postValue(isInList())
            }
        }}

    suspend fun addStock(stock: Stock) {
        interactors.addStock(stock)
    }

    suspend fun removeStock(stock: Stock) {
        interactors.removeStock(stock)
    }

    suspend fun isInList() = interactors.getStocks().any { it.symbol == symbol.value }
}