package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private var apiInterface: APIInterface? = null
    private var code = ""

    lateinit var edZipCode: EditText
    lateinit var getButton: Button

    lateinit var clUser: ConstraintLayout
    lateinit var clMain: ConstraintLayout

    lateinit var tvCity: TextView
    lateinit var tvDateTime: TextView

    lateinit var tvSky: TextView
    lateinit var tvTemp: TextView
    lateinit var tvLow: TextView
    lateinit var tvHigh: TextView

    lateinit var tvSunRiseTime: TextView
    lateinit var tvSunSetTime: TextView
    lateinit var tvWind: TextView
    lateinit var tvPressure: TextView
    lateinit var tvHumidity: TextView
    lateinit var llRefresh: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeVars()

        getButton.setOnClickListener {
            try {
                if (edZipCode.text.isEmpty())
                    Toast.makeText(this, "No Code Entered", Toast.LENGTH_LONG).show()
                else if (edZipCode.text.toString().toInt() > 99950 || edZipCode.text.toString()
                        .toInt() < 1
                ) {
                    Toast.makeText(
                        this,
                        "Invalid ZIP Code, will display New york",
                        Toast.LENGTH_LONG
                    ).show()
                    code = "10001,us"
                    getWeatherByZIP()
                } else {
                    code = edZipCode.text.toString()
                    getWeatherByZIP("$code,us")
                }
            } catch (e: Exception) {
                code = "10001,us"
                getWeatherByZIP()
            }
        }

        tvCity.setOnClickListener {
            hideMainShowUser()
        }

        llRefresh.setOnClickListener {
            refresh()
        }
    }

    private fun initializeVars() {
        edZipCode = findViewById(R.id.edZIPCode)
        getButton = findViewById(R.id.getButton)
        clMain = findViewById(R.id.clMain)
        clUser = findViewById(R.id.clUser)
        tvCity = findViewById(R.id.tvCity)
        tvDateTime = findViewById(R.id.tvTimeAndDate)
        tvSky = findViewById(R.id.tvSky)
        tvTemp = findViewById(R.id.tvTemp)
        tvLow = findViewById(R.id.tvLowTemp)
        tvHigh = findViewById(R.id.tvHighTemp)
        tvSunRiseTime = findViewById(R.id.tvSunRiseTime)
        tvSunSetTime = findViewById(R.id.tvSunSetTime)
        tvWind = findViewById(R.id.tvWind)
        tvPressure = findViewById(R.id.tvPressure)
        tvHumidity = findViewById(R.id.tvHumidity)
        llRefresh = findViewById(R.id.llRefresh)
        apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
    }


    private fun getWeatherByZIP(zip: String = "10001,us") {
        hideUserShowMain()
        apiInterface?.getWeatherByZIP(zip)?.enqueue(object : Callback<WeatherResponse?> {
            override fun onResponse(
                call: Call<WeatherResponse?>,
                response: Response<WeatherResponse?>
            ) {
                if (response.isSuccessful) {
                    updateView(response.body())
                } else {
                    code = "10001,us"
                    getWeatherByZIP()
                }
            }

            override fun onFailure(call: Call<WeatherResponse?>, t: Throwable) {
                println(t.message)
            }
        })
    }

    private fun hideUserShowMain() {
        clMain.isVisible = true
        clUser.isVisible = false
    }

    private fun hideMainShowUser() {
        clMain.isVisible = false
        clUser.isVisible = true
    }

    private fun updateView(weatherResponse: WeatherResponse?) {
        tvCity.text = "${weatherResponse?.name}, ${weatherResponse?.sys?.country}"
        tvDateTime.text = "Updated at: ${simpleDateTime(weatherResponse?.dt)}"
        tvSky.text = weatherResponse!!.weather[0].description.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        tvTemp.text = weatherResponse.main.temp.roundToInt().toString()
        tvLow.text = "Low: ${weatherResponse.main.temp_min.roundToInt()} °C"
        tvHigh.text = "High: ${weatherResponse.main.temp_max.roundToInt()} °C"
        tvSunRiseTime.text = simpleTime(weatherResponse.sys.sunrise)
        tvSunSetTime.text = simpleTime(weatherResponse.sys.sunset)
        tvWind.text = weatherResponse.wind.speed.toString()
        tvPressure.text = weatherResponse.main.pressure.toString()
        tvHumidity.text = weatherResponse.main.humidity.toString()
    }

    private fun simpleTime(dt: Long?): String? {
        return SimpleDateFormat("hh:mm a").format(Date(dt!!.times(1000)))
    }

    private fun simpleDateTime(dt: Long?): String? {
        return SimpleDateFormat("dd/MM/yyyy hh:mm a").format(Date(dt!!.times(1000)))
    }


    private fun refresh() {
        getWeatherByZIP(code)
    }

}