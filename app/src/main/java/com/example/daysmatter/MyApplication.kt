package com.example.daysmatter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        //聚合数据的api
        const val APPKEY="b35989dd6db40fb536d8be42f3202b27"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}