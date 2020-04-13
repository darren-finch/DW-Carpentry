package com.all.dwcarpentry.data

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.google.firebase.database.*

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
            override fun onUploadedHouseImages(imageUrls: List<String>, imagesNames: List<String>)
            {
                firebaseDatabaseAccessor.insertHouseImagesIntoDB(houseKey, imageUrls, imagesNames)
            }
        })
        firebaseStorageAccessor.uploadHouseImages(images)
    }
    //endregion
    //region Firebase Database Operations
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
    abstract class OnChildAddedListener : ChildEventListener
    {
        override fun onCancelled(p0: DatabaseError) {}
        final override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        final override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        final override fun onChildRemoved(p0: DataSnapshot) {}
    }
}