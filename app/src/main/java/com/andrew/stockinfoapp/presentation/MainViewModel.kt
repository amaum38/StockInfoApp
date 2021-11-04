package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.Result
import com.andrew.stockinfoapp.domain.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel: ViewModel(), KoinComponent {
    var searchString = MutableLiveData<String>()

    fun getStocksFromQuery(): Result {
        val api: Endpoints by inject()
        val response = searchString.value?.let { api.getSymbols(it, Constants.API_KEY_1).execute() }
        if (response != null) {
            if (response.isSuccessful) {
                val stocks = response.body()?.bestMatches
                if (stocks != null && stocks.isNotEmpty()) {
                    return Result.Success().also {
                        it.data = stocks
                    }
                } else {
                    return Result.Failure("No results found")
                }
            } else {
                return Result.Failure(response.message())
            }
        } else {
            return Result.Failure("No results found")
        }
    }
}