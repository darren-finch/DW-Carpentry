package com.all.dwcarpentry.data

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.all.dwcarpentry.helpers.Constants
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*

/*
* THE FOLLOWING OPERATIONS MUST BE PRESENT IN THE REPO
* INSERT new house
* UPDATE  (including deletion of Firebase Storage images)
* DELETE house
* GET house WITH key
* UPLOAD house image
* DOWNLOAD house image
* */
class HouseRepository()
{
    private val firebaseDatabaseRef = FirebaseDatabase.getInstance().reference.child("houses")
    private val firebaseStorageRef = FirebaseStorage.getInstance().reference.child("images/all")
    private val allHousesMutable: MutableLiveData<MutableList<House>> = MutableLiveData()

    //region Firebase Storage
    suspend fun uploadHouseImages(images: List<Bitmap>, houseKey: String)
    {
        val imageUrls = mutableListOf<String>()
        val imageNames = mutableListOf<String>()

        for(bm in images)
        {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 75, baos)
            val data = baos.toByteArray()
            val uuid = UUID.randomUUID().toString()
            val ref = firebaseStorageRef.child(uuid)
            val taskSnapshot = ref.putBytes(data).await()
            if(taskSnapshot != null)
            {
                val url = ref.downloadUrl.await().toString()
                imageUrls.add(url)
                imageNames.add(uuid)
            }
        }

        insertHouseImagesIntoDB(houseKey, imageUrls, imageNames)
    }
    private fun deleteImageFromStorage(imageUUID: String)
    {
        try
        {
            firebaseStorageRef.child(imageUUID).delete()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
    private fun deleteHouseImagesFromStorage(deletedHomeImageNames: List<String>)
    {
        for(uuid in deletedHomeImageNames)
        {
            try
            {
                if(uuid.isEmpty())
                    deleteImageFromStorage(uuid)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
    //endregion
    //region Firebase Database Operations
    fun getHouses() : LiveData<MutableList<House>>
    {
        firebaseDatabaseRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot)
            {
                return allHousesMutable.postValue(toHouses(p0))
            }
        })
        return allHousesMutable
    }
    private fun toHouses(snapshot: DataSnapshot): MutableList<House>
    {
        val houses = mutableListOf<House>()
        for (houseSnapshot in snapshot.children)
        {
            val house = houseSnapshot.getValue(House::class.java)
            if (house != null)
            {
                house.key = houseSnapshot.key.toString()
                houses.add(house)
            }
        }
        return houses
    }
    fun insertHouse(newHouse: House)
    {
        val ref = firebaseDatabaseRef.push()
        newHouse.key = ref.key.toString()
        ref.setValue(newHouse)
    }
    fun updateHouse(newData: House, deletedHomeImageNames: List<String>)
    {
        firebaseDatabaseRef.child(newData.key).setValue(newData)
        deleteHouseImagesFromStorage(deletedHomeImageNames)
    }
    fun deleteHouse(houseKey: String)
    {
        val homeImagesNamesRef = firebaseDatabaseRef.child(houseKey).child(Constants.homeImagesNames)
        val dbQuery = homeImagesNamesRef.orderByKey()
        dbQuery.addChildEventListener(object: OnChildAddedListener()
        {
            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                if(!p0.exists()) return
                try
                {
                    val storageRef = firebaseStorageRef.child(p0.value.toString())
                    storageRef.delete()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        })
        firebaseDatabaseRef.child(houseKey).removeValue()
    }
    private fun insertHouseImagesIntoDB(houseKey: String, imageUrls: List<String>, imageNames: List<String>)
    {
        println("debug: Inserting imageUrls and imageNames into Firebase Database.")
        val houseRef = firebaseDatabaseRef.child(houseKey)
        houseRef.addValueEventListener(object : ValueEventListener
        {
            var updated = false
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot)
            {
                if(updated)
                    return
                val house = p0.getValue(House::class.java) ?: return
                house.homeImagesUrls.addAll(imageUrls)
                house.homeImagesNames.addAll(imageNames)
                houseRef.setValue(house)
                updated = true
            }
        })
        firebaseDatabaseRef.child(houseKey).child(Constants.homeImagesNames).setValue(imageNames)
    }
    //endregion
    abstract class OnChildAddedListener : ChildEventListener
    {
        override fun onCancelled(p0: DatabaseError)
        {
            println("The OnChildAddedListener operation was cancelled.")
        }
        final override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        final override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        final override fun onChildRemoved(p0: DataSnapshot) {}
    }
}