package com.example.daysmatter.ai.network

import android.util.Log
import com.example.daysmatter.ui.notifications.HistoryCreator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AICreator {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        
    fun <T> create(serviceClass:Class<T>):T= retrofit.create(serviceClass)
    inline fun <reified T>create():T=create(T::class.java)
}