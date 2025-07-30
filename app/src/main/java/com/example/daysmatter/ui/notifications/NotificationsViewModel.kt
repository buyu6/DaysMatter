package com.example.daysmatter.ui.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.daysmatter.ui.home.Repository
import com.example.daysmatter.ui.notifications.Entity.HistoryEvent
import java.time.LocalDate

class NotificationsViewModel : ViewModel() {
    val today=LocalDate.now()
   val date = "${today.monthValue}/${today.dayOfMonth}"
    val historyList = Repository.searchHistory(date)
}