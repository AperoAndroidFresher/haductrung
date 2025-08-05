package com.example.haductrung.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromString(value: String): List<Int> {
        return value.split(',').mapNotNull { it.toIntOrNull() }
    }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return list.joinToString(",")
    }
}