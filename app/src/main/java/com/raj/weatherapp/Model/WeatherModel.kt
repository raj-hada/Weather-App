package com.raj.weatherapp.Model

data class WeatherModel(
    val base: String,
    val clouds: Clouds,
    val cod: String,
    val coord: Coord,
    val dt: String,
    val id: String,
    val main: Main,
    val name: String,
    val sys: Sys,
    val timezone: String,
    val visibility: String,
    val weather: List<Weather>,
    val wind: Wind
)