package com.all.dwcarpentry

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.data.HouseRepository

class MainViewModel(private val repo: HouseRepository) : ViewModel()
{
    suspend fun uploadHouseImages(images: List<Bitmap>, houseKey: String)
    {
        repo.uploadHouseImages(images, houseKey)
    }
    fun getHouses() : LiveData<MutableList<House>>
    {
        return repo.getHouses()
    }
    fun getHouse(houseKey: String) : LiveData<House>
    {
        return repo.getHouse(houseKey)
    }
    fun insertHouse(newHouse: House)
    {
        repo.insertHouse(newHouse)
    }
    fun updateHouse(newData: House, deletedImageNames: List<String>)
    {
        repo.updateHouse(newData, deletedImageNames)
    }
    fun deleteHouse(houseKey: String)
    {
        repo.deleteHouse(houseKey)
    }
}
