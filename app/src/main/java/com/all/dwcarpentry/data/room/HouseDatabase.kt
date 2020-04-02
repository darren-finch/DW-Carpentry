package com.all.dwcarpentry.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.helpers.Converters

@Database(entities = [House::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HouseDatabase: RoomDatabase()
{
    abstract fun getHouseDao(): HouseDao

    companion object
    {
        fun getInstance(context: Context) : HouseDatabase
        {
            return Room.databaseBuilder(context, HouseDatabase::class.java, "houses")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}