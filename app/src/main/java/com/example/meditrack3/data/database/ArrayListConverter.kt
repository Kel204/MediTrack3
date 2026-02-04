package com.example.meditrack3.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ArrayListConverter {

    private val gson = Gson()

//    @TypeConverter
//    fun fromStringList(value: List<String>?): String {
//        return gson.toJson(value ?: emptyList())
//    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()

        val type: Type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }
}
