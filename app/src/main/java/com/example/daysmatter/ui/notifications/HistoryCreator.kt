package com.example.daysmatter.ui.notifications

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HistoryCreator {
    private const val BASE_URL="https://v.juhe.cn/"

    private val retrofit= Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    // 泛型函数：通过 Class 创建 Retrofit Service 实例
    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    // Kotlin 内联 + reified 关键字，调用更方便
    inline fun <reified T> create(): T = create(T::class.java)
}