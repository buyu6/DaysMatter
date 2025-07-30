package com.example.daysmatter.ui.home

import android.util.Log
import androidx.lifecycle.liveData
import androidx.room.TypeConverter
import com.example.daysmatter.ui.notifications.Entity.HistoryEvent
import com.example.daysmatter.ui.notifications.HistoryNetwork
import com.example.daysmatter.ui.notifications.HistoryNetwork.today
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import kotlin.coroutines.CoroutineContext

object Repository {
    //类型转化器
    class Converters {
        @TypeConverter
        fun fromLocalDate(date: LocalDate): String {
            return date.toString() // "2025-07-11"
        }

        @TypeConverter
        fun toLocalDate(dateString: String): LocalDate {
            return LocalDate.parse(dateString)
        }
    }
    
    fun searchHistory(date:String)= fire(Dispatchers.IO){
        val historyResponse=HistoryNetwork.searchHistory(date)
        if (historyResponse.error_code==0&&historyResponse.reason=="success"){
            val history=historyResponse.result
            Result.success(history)
        }else{
            Result.failure(RuntimeException("response status is ${historyResponse.reason}"))
        }
    }
    
    // 将网络请求封装为liveData 方便观察数据变化        
    private fun<T>fire(context: CoroutineContext, block:suspend ()->Result<T>)= liveData<Result<T>>{
        val result=try {
            block()
        }catch (e:Exception){
            Log.e("Repository", "执行网络请求失败", e)
            Result.failure<T>(e)
        }
        emit(result)
    }
}