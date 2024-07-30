package com.raj.weatherapp.api

import androidx.lifecycle.ViewModelProvider

sealed class NetworkResponse<out T> {

    data class Success<out T>(val data : T) : NetworkResponse<T>()

    data class Failure(val message: String) : NetworkResponse<Nothing>()
    data object Loading : NetworkResponse<Nothing>()
}