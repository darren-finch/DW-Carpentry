package com.all.dwcarpentry.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HouseDao
{
    @Query("SELECT * FROM houses")
    fun getAllHouses() : LiveData<List<House>>

    @Query("SELECT * FROM houses WHERE id = :houseId")
    fun getHouse(houseId: Int) : House

    @Insert
    fun insertHouse(house: House)

    @Update
    fun updateHouse(house: House)

    @Delete
    fun deleteHouse(house: House)
}