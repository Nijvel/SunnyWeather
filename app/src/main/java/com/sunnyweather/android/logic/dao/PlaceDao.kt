package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place

object PlaceDao {
    fun savePlace(place: Place) {
        // 自动提交，默认使用apply方式
        sharedPreferences().edit { putString("place", Gson().toJson(place)) }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}