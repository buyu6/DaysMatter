package com.example.daysmatter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        const val APPID="jnokxafgokripkhd"
        const val APPSECRET="GY9h0qmKSwaQP943gEELCMQDltmq5BrV"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}