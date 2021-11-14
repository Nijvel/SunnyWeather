package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel : ViewModel() {
    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    // searchLiveData发生变化，会调用转换函数，执行Repository.searchPlaces(query)
    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }

    // 调用searchPlaces导致searchLiveData发生变化，从而调用转换函数
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }


    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()

    fun savePlace(place: Place) = Repository.savePlace(place)

}