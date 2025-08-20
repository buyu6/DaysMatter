package com.example.daysmatter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.util.Log

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = "可以在此添加分类以便更好地管理"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}