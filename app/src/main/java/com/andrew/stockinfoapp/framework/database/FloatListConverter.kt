package com.andrew.stockinfoapp.framework.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class FloatListConverter {
    var gson = Gson()
    @TypeConverter
    fun stringToList(data: String?): List<Float> {
        val listType: Type? = object : TypeToken<List<Float?>?>() {}.type
        return if (data != null) gson.fromJson(data, listType) else emptyList()
    }

    @TypeConverter
    fun listToString(someObjects: List<Float?>?): String {
        return gson.toJson(someObjects)
    }
}