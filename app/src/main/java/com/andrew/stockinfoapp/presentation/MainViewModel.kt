package com.andrew.stockinfoapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andrew.stockinfoapp.domain.SearchableStockItems
import com.andrew.stockinfoapp.framework.Constants
import com.andrew.stockinfoapp.framework.Endpoints
import com.andrew.stockinfoapp.framework.ResponseInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel(), KoinComponent {
    var searchString = MutableLiveData<String>()

    fun getStocksFromQuery(responseInterface: ResponseInterface) {
        val api: Endpoints by inject()
        searchString.value?.let { api.getSymbols(it, Constants.API_KEY_1) }
            ?.enqueue(object : Callback<SearchableStockItems> {
                override fun onResponse(
                    call: Call<SearchableStockItems>,
                    response: Response<SearchableStockItems>
                ) {
                    if (response.isSuccessful) {
                        val stocks = response.body()?.bestMatches
                        if (stocks != null && stocks.isNotEmpty()) {
                            responseInterface.onSucess(stocks)
                        }
                    } else {
                        responseInterface.onFailure("No results")
                    }
                }

                override fun onFailure(call: Call<SearchableStockItems>, t: Throwable) {
                    t.message?.let { responseInterface.onFailure(it) }
                }
            })
    }
}