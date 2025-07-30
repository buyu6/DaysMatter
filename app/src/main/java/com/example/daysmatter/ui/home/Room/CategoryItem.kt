package com.example.daysmatter.ui.home.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryItem(
    @PrimaryKey(autoGenerate = true) val id:Long=0,
    val name: String,
    val imageId: Int,
    var isSelected:Boolean=false
                            )