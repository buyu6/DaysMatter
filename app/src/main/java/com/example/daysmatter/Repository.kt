package com.example.daysmatter

import android.app.DatePickerDialog
import androidx.room.TypeConverter
import java.time.LocalDate

object Repository {
    //类型转化器
    class Converters {
        @TypeConverter
        fun fromLocalDate(date: LocalDate): String {
            return date.toString() // "2025-07-11"
        }

        @TypeConverter
        fun toLocalDate(dateString: String): LocalDate {
            return LocalDate.parse(dateString)
        }
    }
}