package com.andrew.stockinfoapp.interactors.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class FloatListConverter {
    var gson = Gson()
    @TypeConverter
    fun stringToList(data: String?): List<Float> {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type? = object : TypeToken<List<Float?>?>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listToString(someObjects: List<Float?>?): String {
        return gson.toJson(someObjects)
    }
}