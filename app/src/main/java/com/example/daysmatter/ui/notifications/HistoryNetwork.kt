package com.example.daysmatter.ui.notifications

import android.util.Log
import com.example.daysmatter.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import retrofit2.http.Query
import java.time.LocalDate
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object HistoryNetwork {
    val today=LocalDate.now()
    private val historyService=HistoryCreator.create<HistoryService>()
    
    suspend fun searchHistory(date:String)= historyService.searchHistory(date).await()
    
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine {
                continuation ->
            Log.d("HistoryNetwork", "开始网络请求，日期: ${(today.month+1)}/${today.dayOfMonth}")
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    Log.e("HistoryNetwork", "网络请求失败: ${t.message}", t)
                    continuation.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    Log.d("HistoryNetwork", "收到响应: code=${response.code()}, message=${response.message()}")
                    val historyList = response.body()
                    if (historyList != null) {
                        Log.d("HistoryNetwork", "响应体不为空，数据获取成功")
                        continuation.resume(historyList)
                    } else {
                        Log.e("HistoryNetwork", "响应体为空")
                        continuation.resumeWithException(RuntimeException("response body is null"))
                    }
                }
            })
        }
    }
}