package com.all.dwcarpentry.helpers

import android.net.Uri
import androidx.room.TypeConverter

class Converters
{
    @TypeConverter
    fun convertListIntoString(input: List<String>) : String
    {
        return input.joinToString(",")
    }
    @TypeConverter
    fun convertStringIntoList(input: String) : List<String>
    {
        return input.split(",")
    }
}