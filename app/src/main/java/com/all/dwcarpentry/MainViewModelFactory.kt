package com.all.dwcarpentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.all.dwcarpentry.data.HouseRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(val repo: HouseRepository) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        return MainViewModel(repo) as T
    }
}