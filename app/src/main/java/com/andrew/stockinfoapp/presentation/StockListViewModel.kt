package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.Stock
import com.andrew.stockinfoapp.framework.Interactors
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class StockListViewModel() : ViewModel(), KoinComponent {
    val interactors: Interactors by inject()

    val stocks = MediatorLiveData<List<Stock>>().apply {
        GlobalScope.launch {
            postValue(interactors.getStocks())
        }
    }
}