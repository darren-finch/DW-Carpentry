package com.all.dwcarpentry.data

import androidx.lifecycle.LiveData
import com.all.dwcarpentry.data.room.House

interface IRepository
{
    fun getAllHouses() : LiveData<List<House>>
    suspend fun getHouse(houseId: Int) : LiveData<House>
    suspend fun insertHouse(house: House)
    suspend fun updateHouse(house: House)
    suspend fun deleteHouse(house: House)
}