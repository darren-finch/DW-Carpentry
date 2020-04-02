package com.all.dwcarpentry.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.all.dwcarpentry.data.room.HouseDatabase
import com.all.dwcarpentry.helpers.Constants
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/*
* THE FOLLOWING OPERATIONS MUST BE PRESENT IN THE REPO
* INSERT new house
* UPDATE  (including deletion of Firebase Storage images)
* DELETE house
* GET house WITH key
* UPLOAD house image
* DOWNLOAD house image
* */
class HouseRepository(private val houseDBRef: HouseDatabase)
{
    private val firebaseDatabaseRef = FirebaseDatabase.getInstance().reference.child("houses")
    private val firebaseStorageRef = FirebaseStorage.getInstance().reference.child("images/all")
    private val allHousesMutable: MutableLiveData<MutableList<House>> = MutableLiveData()

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

    fun getHouses() : LiveData<MutableList<House>>
    {
        firebaseDatabaseRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) { }
            override fun onDataChange(p0: DataSnapshot)
            {
                return allHousesMutable.postValue(toHouses(p0))
            }
        })
        return allHousesMutable
    }
    fun getHouseWithKey(houseKey: String) : House?
    {
        return houseDBRef.getHouseDao().getHouseWithKey(houseKey)
    }

    fun insertHouse(newHouse: House)
    {
        val ref = firebaseDatabaseRef.push()
        newHouse.key = ref.key.toString()
        ref.setValue(newHouse)

        GlobalScope.launch(Dispatchers.IO) {
            insertHouseInDB(newHouse)
        }
    }
    private suspend fun insertHouseInDB(newHouse: House)
    {
        houseDBRef.getHouseDao().insertHouse(newHouse)
    }

    fun updateHouse(newData: House, deletedHomeImageUrls: List<String>, deletedHomeImageNames: List<String>)
    {
        firebaseDatabaseRef.child(newData.key).setValue(newData)
        GlobalScope.launch(Dispatchers.IO) {
            updateHouseInDB(newData)
        }
    }
    private suspend fun updateHouseInDB(newData: House)
    {
        houseDBRef.getHouseDao().updateHouse(newData)
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
                val storageRef = firebaseStorageRef.child(p0.value.toString())
                storageRef.delete()
            }
        })
        firebaseDatabaseRef.child(houseKey).removeValue()
        GlobalScope.launch(Dispatchers.IO) {
            deleteHouseInDB(houseKey)
        }
    }
    private suspend fun deleteHouseInDB(houseKey: String)
    {
        val house = houseDBRef.getHouseDao().getHouseWithKey(houseKey)
        houseDBRef.getHouseDao().deleteHouse(house)
    }

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