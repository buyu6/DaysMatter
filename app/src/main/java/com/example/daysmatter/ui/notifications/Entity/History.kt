package com.example.daysmatter.ui.notifications.Entity

import java.time.Year

data class HistoryResponse(val reason:String,val result:List<HistoryEvent>,val error_code: Int)
data class HistoryEvent(val date:String,val title:String)
