package com.andrew.stockinfoapp.domain

sealed class Result {
    class Success() : Result() {
        var data: List<Any> = emptyList()
    }
    data class Failure(val errorMessage: String) : Result()
}