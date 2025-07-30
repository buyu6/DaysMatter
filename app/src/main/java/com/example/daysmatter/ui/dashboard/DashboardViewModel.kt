package com.example.daysmatter.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daysmatter.MyApplication
import com.example.daysmatter.ui.home.Room.CategoryItem
import com.example.daysmatter.ui.home.Room.Message
import com.example.daysmatter.ui.home.Room.MessageDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardViewModel : ViewModel() {

    private val _msgList = MutableLiveData<List<CategoryItem>>()
    val msgList: LiveData<List<CategoryItem>> get() = _msgList
    private val dao = MessageDatabase.getDatabase(MyApplication.context).messageDao()
    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {  // 切到IO线程
            val categories = dao.loadAllcategory()
            withContext(Dispatchers.Main) {
                //切回主线程更新UI，比如 LiveData 或 Statlow
                _msgList.value =categories
            }
        }
    }
}