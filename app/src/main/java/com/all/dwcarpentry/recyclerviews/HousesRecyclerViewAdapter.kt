package com.all.dwcarpentry.recyclerviews

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.all.dwcarpentry.R
import com.all.dwcarpentry.data.House

class HousesRecyclerViewAdapter(private val allHouses: MutableList<House>,
                                private val onHouseCardClickedListener: OnHouseCardClickedListener) : RecyclerView.Adapter<HouseViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.house_card, parent, false)
        return HouseViewHolder(view, onHouseCardClickedListener)
    }

    override fun getItemCount(): Int
    {
        return allHouses.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HouseViewHolder, position: Int)
    {
        val house = allHouses[position]
        holder.houseKey = house.key
        holder.homeOwnerName.text = "Home Owner - " + house.homeOwnerName
        holder.homeAddress.text = "Address - " + house.homeAddress
    }

    fun updateHouses(newHouses: MutableList<House>)
    {
        println("newHouses length is ${newHouses.size}")
        allHouses.clear()
        allHouses.addAll(newHouses)
        notifyDataSetChanged()
    }

    interface OnHouseCardClickedListener
    {
        fun onHouseCardClicked(houseKey: String)
    }
}