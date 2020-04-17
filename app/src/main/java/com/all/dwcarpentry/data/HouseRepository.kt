package com.all.dwcarpentry.data

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData

/*
* THE FOLLOWING OPERATIONS MUST BE PRESENT IN THE REPO
* INSERT new house
* UPDATE  (including deletion of Firebase Storage images)
* DELETE house
* GET house WITH key
* UPLOAD house image
* DOWNLOAD house image
* */
class HouseRepository(private val firebaseDatabaseAccessor: FirebaseDatabaseAccessor, private val firebaseStorageAccessor: FirebaseStorageAccessor)
{
    //region Firebase Storage
    suspend fun uploadHouseImages(images: List<Bitmap>, houseKey: String)
    {
        firebaseStorageAccessor.setListener(object : FirebaseStorageAccessor.Listener{
            override fun onUploadedHouseImage(imageUrl: String, imageName: String)
            {
                Log.i("HouseRepository", "Inserting uploaded image into $houseKey")
                firebaseDatabaseAccessor.insertHouseImageIntoDB(houseKey, imageUrl, imageName)
            }
        })
        firebaseStorageAccessor.uploadHouseImages(images)
    }
    //endregion
    //region Firebase Database Operations
    fun loadMoreHouses()
    {
        firebaseDatabaseAccessor.loadMoreHouses()
    }
    fun getHouses() : LiveData<MutableList<House>>
    {
        return firebaseDatabaseAccessor.getHouses()
    }
    fun getHouse(houseKey: String) : LiveData<House>
    {
        return firebaseDatabaseAccessor.getHouse(houseKey)
    }
    fun insertHouse(house: House) : String
    {
        return firebaseDatabaseAccessor.insertHouse(house)
    }
    fun updateHouse(house: House, deletedImageNames: List<String>)
    {
        firebaseDatabaseAccessor.updateHouse(house)
        firebaseStorageAccessor.deleteHouseImagesFromStorage(deletedImageNames)
    }
    fun deleteHouse(houseKey: String)
    {
        firebaseDatabaseAccessor.setListener(object : FirebaseDatabaseAccessor.Listener
        {
            override fun deletedHouse(deletedImageNames: List<String>)
            {
                firebaseStorageAccessor.deleteHouseImagesFromStorage(deletedImageNames)
            }
        })
        firebaseDatabaseAccessor.deleteHouse(houseKey)
    }
    //endregion
}