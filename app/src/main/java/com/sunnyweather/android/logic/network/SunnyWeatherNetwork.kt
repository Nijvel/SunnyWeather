package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.Weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {

    private val placeService = ServiceCreator.create<PlaceService>()
    private val weatherService = ServiceCreator.create<WeatherService>()

    // 在协程作用域中调用此函数，通过await函数将调用的协程挂起，当网络请求返回的时候，唤醒被挂起的协程
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    // 因为不同的Service接口返回的数据类型可能不同，所以写成这样方便复用。定义成Call<T>的扩展函数，也是方便调用。
    // 在await函数中直接通过enqueue进行网络请求
    private suspend fun <T> Call<T>.await(): T {
        /**
         * suspendCoroutine必须在协程作用域或挂起函数中才能调用。
         * 作用：将当前协程立刻挂起，然后在一个普通的线程中执行Lambda表达式中的代码。
         * 在Lambda中通过调用continuation的resume()和resumeWithException()让协程恢复运行
         */
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        // 如果获取body成功，则恢复外部被挂起的协程
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}