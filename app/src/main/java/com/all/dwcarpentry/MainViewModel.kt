package com.all.dwcarpentry

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.all.dwcarpentry.data.House
import com.all.dwcarpentry.data.HouseRepository

class MainViewModel(private val repo: HouseRepository) : ViewModel()
{
    fun getHouses() : LiveData<MutableList<House>>
    {
        return repo.getHouses()
    }
    fun getHouseWithKey(houseKey: String) : House?
    {
        return repo.getHouseWithKey(houseKey)
    }
}
