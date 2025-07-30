package com.example.daysmatter.ui.home.Room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.daysmatter.ui.home.Repository

@Database(version = 1, entities = [Message::class,CategoryItem::class])
@TypeConverters(Repository.Converters::class)
abstract class MessageDatabase:RoomDatabase() {
    abstract fun messageDao():MessageDao
    companion object{
        private var instance:MessageDatabase?=null
        @Synchronized
        fun getDatabase(context: Context):MessageDatabase{
            instance?.let {
                return  it
            }
            return Room.databaseBuilder(context.applicationContext,MessageDatabase::class.java,"message_database")
                .build().apply {
                    instance=this
                }
        }
    }
}