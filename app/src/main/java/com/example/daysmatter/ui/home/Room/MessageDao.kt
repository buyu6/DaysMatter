package com.example.daysmatter.ui.home.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MessageDao {
    @Insert
     fun insertMessage(msg:Message):Long
    @Update
     fun updateMessage(newmsg:Message)
    @Delete
     fun deletemessage(message: Message)
    @Query("select * from message")
     fun loadAllMessage():List<Message>
    @Query("DELETE FROM message WHERE id =:id")
    fun deleteById(id: Long)
    @Query("SELECT * FROM message ORDER BY isTop DESC, id ASC")
    fun getAllByTop(): List<Message>
    @Query("UPDATE message SET isTop = false WHERE isTop = true")
    fun cancelAllTop()
    // 查询某个分类下的所有消息
    @Query("SELECT * FROM message WHERE category = :category ORDER BY isTop DESC, id ASC")
    fun getMessagesByCategory(category: String): List<Message>
}