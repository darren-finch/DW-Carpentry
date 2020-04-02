package com.all.dwcarpentry.data.room

import androidx.room.*
import com.all.dwcarpentry.data.House

@Dao
interface HouseDao
{
    @Query("SELECT 1 FROM houses WHERE `key` = :houseKey")
    fun getHouseWithKey(houseKey: String) : House
    @Insert
    fun insertHouse(newHouse: House)
    @Update
    fun updateHouse(newData: House)
    @Delete
    fun deleteHouse(house: House)
}