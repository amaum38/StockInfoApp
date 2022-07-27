package com.andrew.stockinfoapp.domain

sealed class NetworkResult {
    data class Success(val data: Any) : NetworkResult()
    data class Failure(val errorMessage: String) : NetworkResult()
}
