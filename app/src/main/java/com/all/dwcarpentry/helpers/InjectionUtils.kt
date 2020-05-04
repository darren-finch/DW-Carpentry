package com.all.dwcarpentry.helpers

import android.app.Application
import android.content.Context
import com.all.dwcarpentry.viewmodels.MainViewModelFactory
import com.all.dwcarpentry.data.HouseRepository
import com.all.dwcarpentry.data.IRepository
import com.all.dwcarpentry.data.room.HouseDatabase

object InjectionUtils
{
    private var repository: IRepository? = null

    fun provideMainViewModelFactory(application: Application, context: Context) : MainViewModelFactory
    {
        if(repository == null)
            repository = HouseRepository(HouseDatabase.getInstance(context).houseDao())

        return MainViewModelFactory(application, repository!!)
    }
}