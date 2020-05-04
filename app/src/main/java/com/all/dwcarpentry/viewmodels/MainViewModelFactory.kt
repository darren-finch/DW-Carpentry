package com.all.dwcarpentry.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.all.dwcarpentry.data.HouseRepository
import com.all.dwcarpentry.data.IRepository

@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val application: Application, private val repo: IRepository) : ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T
    {
        return MainViewModel(application, repo) as T
    }
}