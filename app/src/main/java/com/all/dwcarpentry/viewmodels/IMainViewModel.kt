package com.all.dwcarpentry.viewmodels

import androidx.lifecycle.LiveData
import com.all.dwcarpentry.data.room.House

interface IMainViewModel
{
    fun getAllHouses(): LiveData<List<House>>
    fun getHouse(houseId: Int): LiveData<House>
    fun insertHouse(house: House)
    fun updateHouse(house: House)
    fun deleteHouse(house: House)
}