package com.all.dwcarpentry.helpers

import androidx.room.TypeConverter
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

object Converters
{
    @TypeConverter
    fun stringListToJson(list: MutableList<String>) : String
    {
        return JSONArray(list).toString()
    }
    @TypeConverter
    fun jsonToStringList(json: String) : MutableList<String>
    {
        val gson = GsonBuilder().create()
        val stringArray = gson.fromJson(json , Array<String>::class.java).toList()
        return stringArray.toMutableList()
    }
}