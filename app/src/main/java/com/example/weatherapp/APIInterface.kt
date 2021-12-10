package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {

    @GET("weather")
    fun getWeatherByZIP(
        @Query("zip", encoded = true) zip:String,
        @Query("appid") appid:String = "e840c236f28359e47f483dfbe723beb5",
        @Query("units") units: String ="metric",
    ) : Call<WeatherData?>
}