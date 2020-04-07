package com.all.dwcarpentry.helpers

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.all.dwcarpentry.MainViewModel
import com.all.dwcarpentry.MainViewModelFactory
import com.all.dwcarpentry.data.HouseRepository

object InjectionUtils
{
    private val repository: HouseRepository = HouseRepository()

    fun provideMainViewModelFactory() : MainViewModelFactory
    {
        println("debug: Repository = $repository")
        return MainViewModelFactory(repository)
    }
//    fun reInitRepository()
//    {
//        repository = HouseRepository()
//    }
}