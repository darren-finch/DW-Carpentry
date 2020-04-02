package com.all.dwcarpentry.helpers

import android.content.Context
import com.all.dwcarpentry.MainViewModelFactory
import com.all.dwcarpentry.data.HouseRepository
import com.all.dwcarpentry.data.room.HouseDatabase

object InjectionUtils
{
    fun provideMainViewModelFactory(context: Context) : MainViewModelFactory
    {
        val repo = HouseRepository(HouseDatabase.getInstance(context))
        return MainViewModelFactory(repo)
    }
}