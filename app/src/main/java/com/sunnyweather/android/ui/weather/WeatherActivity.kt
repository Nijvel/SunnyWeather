package com.sunnyweather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        // 从intent中取出坐标和地点，并赋值到viewModel的相应变量中
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        // 设置监听器
        viewModel.weatherLiveData.observe(this, Observer { result ->
            // 如果获取到的数据不为空，则进行展示
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        // 对WeatherViewModel中的数据进行改动，会出发转换函数
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather) {
        val placeName: TextView = findViewById(R.id.placeName)
        val currentTemp: TextView = findViewById(R.id.currentTemp)
        val currentSky: TextView = findViewById(R.id.currentSky)
        val currentAQI: TextView = findViewById(R.id.currentAQI)
        val nowLayout: RelativeLayout = findViewById(R.id.nowLayout)
        val forecastLayout: LinearLayout =  findViewById(R.id.forecastLayout)
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()}"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 先删除所有的view，然后对forecastLayout中的view进行重新填充
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo: TextView = view.findViewById(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        val lifeIndex = daily.lifeIndex
        val coldRiskText: TextView = findViewById(R.id.coldRiskText)
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        val dressingText: TextView = findViewById(R.id.dressingText)
        dressingText.text = lifeIndex.dressing[0].desc
        val ultravioletText: TextView = findViewById(R.id.ultravioletText)
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        val carWashingText: TextView = findViewById(R.id.carWashingText)
        carWashingText.text = lifeIndex.carWashing[0].desc
        val weatherLayout: ScrollView = findViewById(R.id.weatherLayout)
        weatherLayout.visibility = View.VISIBLE
    }
}