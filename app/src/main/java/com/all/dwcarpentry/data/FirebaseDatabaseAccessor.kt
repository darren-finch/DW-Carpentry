package com.all.dwcarpentry.data

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.all.dwcarpentry.helpers.Constants
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

/*
* This class is for all Firebase Database related operations.
* Refactor the HouseRepository to use this object's methods.
*/
class FirebaseDatabaseAccessor(private val firebaseDatabaseRef: DatabaseReference)
{
    private val allHousesMutable: MutableLiveData<MutableList<House>> = MutableLiveData()
//    private val oldestHouseId = ""
    private val curHouse: MutableLiveData<House> = MutableLiveData()

    private lateinit var listener: Listener

//    fun requestMoreHouses() : LiveData<MutableList<House>>
//    {
//        if(oldestHouseId.isNotEmpty())
//        {
//            firebaseDatabaseRef.startAt(oldestHouseId).orderByKey().addValueEventListener(object : ValueEventListener{
//                override fun onCancelled(p0: DatabaseError) {}
//                override fun onDataChange(p0: DataSnapshot)
//                {
//                    return allHousesMutable.postValue(toHouses(p0))
//                }
//            })
//        }
//        else
//        {
//
//        }
//        return allHousesMutable
//    }
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