package com.example.daysmatter.ui.home.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class Message(
    val title: String,              // 标题
    val time: Int,                  // 与今天的天数差（正/负）
    val aimdate: String? ,         // 目标日期，格式如 2025-07-09
    val isTop:Boolean=false,
    @PrimaryKey(autoGenerate = true) val id: Long =0 ,// 主键，自增
    val categoryIcon:Int?=-1,
    val categoryName: String?=null
)
