package com.example.daysmatter.ui.notifications


import com.example.daysmatter.MyApplication
import com.example.daysmatter.ui.notifications.Entity.HistoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDate

interface HistoryService {
    @GET("todayOnhistory/queryEvent.php")
    fun searchHistory( @Query("date") date: String,
                       @Query("key") key: String=MyApplication.APPKEY): Call<HistoryResponse>
}