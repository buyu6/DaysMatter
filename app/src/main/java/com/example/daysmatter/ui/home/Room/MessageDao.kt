package com.example.daysmatter.ui.home.Room

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
}