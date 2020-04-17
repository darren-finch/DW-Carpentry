package com.all.dwcarpentry.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.all.dwcarpentry.helpers.Constants
import com.google.firebase.database.*
import java.util.*

/*
* This class is for all Firebase Database related operations.
* Refactor the HouseRepository to use this object's methods.
*/
class FirebaseDatabaseAccessor(private val firebaseDatabaseRef: DatabaseReference)
{
    private val allHousesMutable: MutableLiveData<MutableList<House>> = MutableLiveData()
    private val curHouse: MutableLiveData<House> = MutableLiveData()
    private var oldestHouseKey = ""
    private lateinit var listener: Listener

    fun loadMoreHouses()
    {
        if(oldestHouseKey.isEmpty())
        {
            firebaseDatabaseRef
                .orderByKey()
                .limitToLast(Constants.PAGE_SIZE)
                .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot)
                {
                    allHousesMutable.postValue(toHouses(p0))
                }
            })
        }
        else
        {
            firebaseDatabaseRef
                .orderByKey()
                .limitToLast(Constants.PAGE_SIZE)
                .endAt(oldestHouseKey)
                .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot)
                {
                    allHousesMutable.postValue(toHouses(p0))
                }
            })
        }
    }
    fun getHouses() : LiveData<MutableList<House>>
    {
        return allHousesMutable
    }
    fun getHouse(houseKey: String) : LiveData<House>
    {
        firebaseDatabaseRef.child(houseKey).addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                    curHouse.postValue(p0.getValue(House::class.java))
            }
        })
        return curHouse
    }
    private fun toHouses(snapshot: DataSnapshot): MutableList<House>
    {
        val newHouses = mutableListOf<House>()
        var currentHouses = mutableListOf<House>()
        if(allHousesMutable.value != null)
            currentHouses = allHousesMutable.value!!

        for (houseSnapshot in snapshot.children)
        {
            val house = houseSnapshot.getValue(House::class.java)
            if (house != null)
            {
                house.key = houseSnapshot.key.toString()
                newHouses.add(house)
            }
        }
        if(newHouses.size > 0)
            oldestHouseKey = newHouses[0].key
        newHouses.reverse()

        currentHouses.addAll(newHouses)
        return currentHouses
    }
    fun insertHouseImageIntoDB(houseKey: String, imageUrl: String, imageName: String)
    {
        if(houseKey.isEmpty())
            return

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
                house.homeImagesUrls.add(imageUrl)
                house.homeImagesNames.add(imageName)
                houseRef.setValue(house)
                updated = true
            }
        })
    }

    fun insertHouse(house: House) : String
    {
        val ref = firebaseDatabaseRef.push()
        val keyString = ref.key.toString()
        house.key = keyString
        ref.setValue(house)
        return keyString
    }

    fun updateHouse(house: House)
    {
        if(house.key.isEmpty())
            house.key = UUID.randomUUID().toString()

        firebaseDatabaseRef.child(house.key).setValue(house)
    }
    fun deleteHouse(houseKey: String)
    {
        var house: House?
        firebaseDatabaseRef.child(houseKey).addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot)
            {
                if(p0.exists())
                {
                    house = p0.getValue(House::class.java)
                    if(house != null)
                        listener.deletedHouse(house!!.homeImagesNames)
                }
            }
        })
        firebaseDatabaseRef.child(houseKey).removeValue()
    }

    fun setListener(listener: Listener)
    {
        this.listener = listener
    }
    interface Listener
    {
        fun deletedHouse(deletedImageNames: List<String>)
    }
}