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

    fun resetPagination()
    {
        allHousesMutable.value = mutableListOf()
        oldestHouseKey = ""
    }
    fun loadMoreHouses()
    {
        if(oldestHouseKey.isEmpty())
        {
            firebaseDatabaseRef
                .orderByKey()
                .limitToLast(Constants.PAGE_SIZE)
                .addListenerForSingleValueEvent(object : ValueEventListener{
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
                .addListenerForSingleValueEvent(object : ValueEventListener{
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
        val newHouses = snapshotToNewHouses(snapshot)
        var currentHouses = mutableListOf<House>()
        if(oldestHouseKey.isNotEmpty())
        {
            newHouses.removeAt(newHouses.lastIndex)
            currentHouses = allHousesMutable.value!!
        }

        newHouses.reverse()

        currentHouses.addAll(newHouses)
        if(currentHouses.lastIndex > -1)
            oldestHouseKey = currentHouses[currentHouses.lastIndex].key

        return currentHouses
    }
    private fun snapshotToNewHouses(snapshot: DataSnapshot): MutableList<House>
    {
        val newHouses = mutableListOf<House>()
        for (houseSnapshot in snapshot.children)
        {
            val house = houseSnapshot.getValue(House::class.java)
            if (house != null)
            {
                house.key = houseSnapshot.key.toString()
                newHouses.add(house)
            }
        }
        return newHouses
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

    fun generateHouses()
    {
        val houses = mutableListOf<House>()
        val homeImageUrls = mutableListOf<String>()
        homeImageUrls.add("https://firebasestorage.googleapis.com/v0/b/dc-carpentry-b7864.appspot.com/o/images%2Fdefault%2Fhouse.jpg?alt=media&token=8ee790fe-06de-4561-905f-7f15b23e74b5")
        homeImageUrls.add("https://firebasestorage.googleapis.com/v0/b/dc-carpentry-b7864.appspot.com/o/images%2Fdefault%2Fhouse2.jpg?alt=media&token=c9c226f1-8bfd-4d9d-9453-0633f16b861a")
        homeImageUrls.add("https://firebasestorage.googleapis.com/v0/b/dc-carpentry-b7864.appspot.com/o/images%2Fdefault%2Fhouse3.jpg?alt=media&token=8afbc4a4-d023-4291-8edb-78dce22772a9")
        val homeImageNames = mutableListOf<String>()
        homeImageNames.add("house")
        homeImageNames.add("house1")
        homeImageNames.add("house2")

        for (i in 1..500)
            houses.add(House("", "Homeowner $i", "Address $i", "", homeImageUrls, homeImageNames))

        firebaseDatabaseRef.setValue(houses).addOnSuccessListener {
            firebaseDatabaseRef.addValueEventListener(object : ValueEventListener {
                var changed = false
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot)
                {
                    if(p0.exists() && !changed)
                    {
                        for(snapshot in p0.children)
                        {
                            val house = snapshot.getValue(House::class.java)
                            if(house != null)
                            {
                                house.key = snapshot.key.toString()
                                snapshot.ref.setValue(house)
                            }
                        }
                        changed = true
                    }
                }
            })
        }
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