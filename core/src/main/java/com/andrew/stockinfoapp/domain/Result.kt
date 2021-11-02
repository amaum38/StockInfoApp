package com.andrew.stockinfoapp.domain

sealed class Result {
    class Success() : Result() {
        lateinit var data: Any
    }
    data class Failure(val errorMessage: String) : Result()
}