package com.example.feature_weather

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.base.ApiClient
import com.example.base.WeatherResponse
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val cityInput: EditText = findViewById(R.id.cityInput)
        val getWeatherButton: Button = findViewById(R.id.getWeatherButton)
        val weatherTextView: TextView = findViewById(R.id.weatherTextView)

        getWeatherButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                fetchWeather(city, weatherTextView)
            } else {
                Toast.makeText(this, "Enter a city", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String, textView: TextView) {
        val apiKey = "your_api_key" // Replace with your actual API key

        ApiClient.instance.getWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    weather?.let {
                        textView.text = "City: ${it.city}\nTemp: ${it.temperature}°C\n${it.description}"
                    }
                } else {
                    textView.text = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                textView.text = "Failed to fetch data"
            }
        })
    }
}
