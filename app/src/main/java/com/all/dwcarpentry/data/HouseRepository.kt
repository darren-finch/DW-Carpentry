package com.all.dwcarpentry.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.all.dwcarpentry.data.room.House
import com.all.dwcarpentry.data.room.HouseDao
import com.all.dwcarpentry.data.room.HouseDatabase

/*
* THE FOLLOWING OPERATIONS MUST BE PRESENT IN THE REPO
* INSERT new house
* UPDATE  (including deletion of Firebase Storage images)
* DELETE house
* GET house WITH key
* UPLOAD house image
* DOWNLOAD house image
* */
class HouseRepository(private val houseDao: HouseDao) : IRepository
{
    private val allHousesLiveData: LiveData<List<House>> = houseDao.getAllHouses()

    override fun getAllHouses(): LiveData<List<House>>
    {
        return allHousesLiveData
    }
    override suspend fun getHouse(houseId: Int): House
    {
        return houseDao.getHouse(houseId)
    }
    override suspend fun insertHouse(house: House)
    {
        houseDao.insertHouse(house)
    }
    override suspend fun updateHouse(house: House)
    {
        houseDao.updateHouse(house)
    }
    override suspend fun deleteHouse(house: House)
    {
        houseDao.deleteHouse(house)
    }
}