package com.example.daysmatter.ui.home.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message")
data class Message(
    var title: String,              // 标题
    var time: Int,                  // 与今天的天数差（正/负）
    var aimdate: String?          // 目标日期，格式如 2025-07-09
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0                // 主键，自增
}
