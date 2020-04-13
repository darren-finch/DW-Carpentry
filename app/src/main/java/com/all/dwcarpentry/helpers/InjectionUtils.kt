package com.all.dwcarpentry.helpers

import com.all.dwcarpentry.MainViewModelFactory
import com.all.dwcarpentry.data.FirebaseDatabaseAccessor
import com.all.dwcarpentry.data.FirebaseStorageAccessor
import com.all.dwcarpentry.data.HouseRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object InjectionUtils
{
    private val repository: HouseRepository = HouseRepository(FirebaseDatabaseAccessor(FirebaseDatabase.getInstance().reference.child("houses")),
        FirebaseStorageAccessor(FirebaseStorage.getInstance().reference.child("images/all")))

    fun provideMainViewModelFactory() : MainViewModelFactory
    {
        println("debug: Repository = $repository")
        return MainViewModelFactory(repository)
    }
}