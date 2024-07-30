package com.raj.weatherapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.weatherapp.Model.WeatherModel
import com.raj.weatherapp.api.Constants
import com.raj.weatherapp.api.NetworkResponse
import com.raj.weatherapp.api.RetrofitInstance
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()

    val weatherResult : LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    fun getData(city : String){

        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {
            try{
                val response = weatherApi.getWeather(Constants.apiKey,city)
                if(response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                }else{
                    _weatherResult.value = NetworkResponse.Failure("Failed to Load Data")
                }
            }catch (e : Exception){
                _weatherResult.value = NetworkResponse.Failure(e.toString())
                Log.d("RAJ", e.toString())
            }
        }

    }
}