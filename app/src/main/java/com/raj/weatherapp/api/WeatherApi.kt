package com.raj.weatherapp.api

import com.raj.weatherapp.Model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {



    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("appid") appid : String,
        @Query("q") city : String
    ) : Response<WeatherModel>

}