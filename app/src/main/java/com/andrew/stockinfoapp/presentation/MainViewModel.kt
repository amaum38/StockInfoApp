package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.framework.Interactors
import com.andrew.stockinfoapp.framework.api.Endpoints
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainViewModel: ViewModel(), KoinComponent {
    var searchString = MutableLiveData("")
    private val interactors: Interactors = get()

    fun getStocksFromQuery() = searchString.value?.let { interactors.searchStocks(it) }
}