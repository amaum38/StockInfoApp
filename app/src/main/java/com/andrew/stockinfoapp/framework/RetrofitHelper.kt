package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.domain.SearchableStockItems
import com.andrew.stockinfoapp.domain.Stock
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Endpoints {
    @GET("query?function=SYMBOL_SEARCH")
    fun getSymbols(@Query("keywords") keywords: String,
                   @Query("apikey") key: String): Call<SearchableStockItems>

    @GET("query?function=OVERVIEW")
    fun getOverview(@Query("symbol") symbol: String,
                    @Query("apikey") key: String): Call<Stock>

    @GET("query?function=TIME_SERIES_INTRADAY")
    fun getPriceData(@Query("symbol") symbol: String,
                     @Query("interval") interval: String,
                     @Query("apikey") key: String): Call<JsonElement>
}

interface ResponseInterface {
    fun onSucess(reversed: List<Float>)
    fun onFailure(code: String)
}

fun provideGson(): GsonConverterFactory {
    return GsonConverterFactory.create()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl("https://www.alphavantage.co/")
        .client(okHttpClient)
        .addConverterFactory(provideGson())
        .build()
}

fun provideApi(retrofit: Retrofit): Endpoints {
    return retrofit.create(Endpoints::class.java)
}