package com.andrew.stockinfoapp.framework

import com.andrew.stockinfoapp.domain.Result
import com.andrew.stockinfoapp.domain.SearchableStockItems
import com.andrew.stockinfoapp.domain.Stock
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
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
    fun onSucess(result: Result)
    fun onFailure(result: Result)
}

fun provideJson(): Converter.Factory {
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    return json.asConverterFactory("application/json".toMediaType())
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().build()
}

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl("https://www.alphavantage.co/")
        .client(okHttpClient)
        .addConverterFactory(provideJson())
        .build()
}

fun provideApi(retrofit: Retrofit): Endpoints {
    return retrofit.create(Endpoints::class.java)
}