package com.example.daysmatter.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daysmatter.MyApplication
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _msgList = MutableLiveData<List<Message>>()
    val msgList: LiveData<List<Message>> get() = _msgList
    private val dao = MessageDatabase.getDatabase(application).messageDao()
    init {
        loadMessages()
    }

    fun loadMessages() {
       viewModelScope.launch(Dispatchers.IO) {  // 切到IO线程
           val messages = dao.getAllByTop()
           withContext(Dispatchers.Main) {
               //切回主线程更新UI，比如 LiveData 或 Statlow
           _msgList.value = messages
       }
    }
}

fun loadMessagesByCategory(category: String) {
    viewModelScope.launch(Dispatchers.IO) {  // 切到IO线程
        val messages = dao.getMessagesByCategory(category)
        withContext(Dispatchers.Main) {
            //切回主线程更新UI，比如 LiveData 或 Statlow
            _msgList.value=messages
        }
    }
    }
}

