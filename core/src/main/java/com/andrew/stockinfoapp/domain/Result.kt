package com.andrew.stockinfoapp.domain

sealed class Result {
    data class Success(val data : List<Any>) : Result()
    data class Failure(val errorMessage: String) : Result()
}