package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.NetworkResult
import com.andrew.stockinfoapp.framework.api.Endpoints
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MainViewModel: ViewModel(), KoinComponent {
    var searchString = MutableLiveData("")

    fun getStocksFromQuery(): NetworkResult {
        val api: Endpoints = get()
        val response = searchString.value?.let { api.getSymbols(it).execute() }
        return if (response != null) {
            if (response.isSuccessful) {
                val stocks = response.body()?.bestMatches ?: emptyList()
                if (stocks.isNotEmpty()) {
                    NetworkResult.Success(stocks)
                } else {
                    NetworkResult.Failure("No results found")
                }
            } else {
                NetworkResult.Failure(response.message())
            }
        } else {
            NetworkResult.Failure("No results found")
        }
    }
}