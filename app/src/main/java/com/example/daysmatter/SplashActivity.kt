package com.example.daysmatter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0,0)
            finish()
        }, 2500) // 延迟 1.5 秒
    }
}