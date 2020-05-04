package com.all.dwcarpentry.data.room

import android.content.Context
import androidx.room.*
import com.all.dwcarpentry.helpers.Converters

@Database(entities = [House::class], views = [], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HouseDatabase : RoomDatabase()
{
    abstract fun houseDao() : HouseDao
    companion object
    {
        @Volatile
        private var INSTANCE: HouseDatabase? = null
        fun getInstance(context: Context) : HouseDatabase
        {
            val tempInstance = INSTANCE
            if(tempInstance != null)
                return tempInstance

            synchronized(this)
            {
                val instance = Room.databaseBuilder(context, HouseDatabase::class.java, "houses").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}