package com.all.dwcarpentry.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.all.dwcarpentry.data.room.House
import com.all.dwcarpentry.data.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val repo: IRepository) : AndroidViewModel(application), IMainViewModel
{
    private val allHousesLiveData: LiveData<List<House>> = repo.getAllHouses()
    private var currentHouseLiveData: LiveData<House> = MutableLiveData()

    override fun getAllHouses(): LiveData<List<House>>
    {
        return allHousesLiveData
    }
    override fun getHouse(houseId: Int): LiveData<House>
    {
        if(currentHouseLiveData.value != null && currentHouseLiveData.value!!.id == houseId)
            return currentHouseLiveData

        viewModelScope.launch (Dispatchers.IO) {
            currentHouseLiveData = repo.getHouse(houseId)
        }
        return currentHouseLiveData
    }
    override fun insertHouse(house: House)
    {
        viewModelScope.launch (Dispatchers.IO) {
            repo.insertHouse(house)
        }
    }
    override fun updateHouse(house: House)
    {
        viewModelScope.launch (Dispatchers.IO) {
            repo.updateHouse(house)
        }
    }
    override fun deleteHouse(house: House)
    {
        viewModelScope.launch (Dispatchers.IO) {
            repo.deleteHouse(house)
        }
    }
}
