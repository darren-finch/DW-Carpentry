package com.all.dwcarpentry.helpers

import com.all.dwcarpentry.MainViewModelFactory
import com.all.dwcarpentry.data.FirebaseDatabaseAccessor
import com.all.dwcarpentry.data.FirebaseStorageAccessor
import com.all.dwcarpentry.data.HouseRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object InjectionUtils
{
    private val repository: HouseRepository = HouseRepository(FirebaseDatabaseAccessor(FirebaseDatabase.getInstance().reference.child(Constants.FIREBASE_DATABASE_HOUSES_REF)),
        FirebaseStorageAccessor(FirebaseStorage.getInstance().reference.child(Constants.FIREBASE_STORAGE_ALL_IMAGES_REF)))

    fun provideMainViewModelFactory() : MainViewModelFactory
    {
        return MainViewModelFactory(repository)
    }
}