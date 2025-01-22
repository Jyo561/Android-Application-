package com.example.base

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeather(@Query("city") city: String, @Query("apikey") apiKey: String): Call<WeatherResponse>
}
