package com.example.daysmatter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        //聚合数据的api
        const val APPKEY="b35989dd6db40fb536d8be42f3202b27"
        const val GEMINI_API_KEY="AIzaSyA8NAnZRZpgXg2TiL6DNlxZhrF77F7flKo"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}